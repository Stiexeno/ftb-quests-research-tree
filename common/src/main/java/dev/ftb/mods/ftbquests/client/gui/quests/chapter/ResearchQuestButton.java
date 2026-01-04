package dev.ftb.mods.ftbquests.client.gui.quests.chapter;

import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ResearchQuestButton extends Button
{
    private final Quest quest;
    
    public ResearchQuestButton(ChapterPanel panel, Chapter c, Quest quest)
    {
        super(panel, quest.getTitle(), quest.getIcon());
        this.quest = quest;
    }
    
    @Override
    public void onClicked(MouseButton button)
    {
        playClickSound();
    }
    
    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h)
    {
        GuiHelper.setupDrawing();
        
        drawBackground(graphics, theme, x, y, w, h);
        icon.withPadding(2).draw(graphics, x, y, w, h);
    }
    
    @Override
    public void addMouseOverText(TooltipList list)
    {
        Component title = getTitle();
        
        var player = Minecraft.getInstance().player;
        
        TeamData teamData = null;
        
        if (player != null)
        {
            teamData = quest.getChapter().file.getTeamData(player).get();
        }
        
        if (teamData != null)
        {
            if (teamData.isStarted(quest) && !teamData.isCompleted(quest))
            {
                title = title.copy().append(Component.literal(" " + teamData.getRelativeProgress(quest) + "%").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        
        if (title.getString().contains("\n"))
        {
            // I'm not proud of this kludge but getting titles with embedded newlines and possible styling
            // to work well as tooltips is not fun
            title.visit((style, txt) ->
            {
                for (String s : txt.split("\n"))
                {
                    if (!s.isEmpty()) list.add(Component.literal(s).withStyle(style));
                }
                return Optional.empty();
            }, title.getStyle());
        }
        else
        {
            list.add(title);
        }
        
        Component description = quest.getSubtitle();
        
        if (!TextUtils.isComponentEmpty(description))
        {
            list.add(description.copy().withStyle(ChatFormatting.GRAY));
        }
        
        if (quest.isOptional())
        {
            list.add(Component.literal("[").withStyle(ChatFormatting.GRAY).append(Component.translatable("ftbquests.quest.misc.optional")).append("]"));
        }
        if (quest.canBeRepeated())
        {
            list.add(Component.translatable("ftbquests.quest.misc.can_repeat").withStyle(ChatFormatting.GRAY));
            if (teamData != null)
            {
                int completionCount = teamData.getCompletionCount(quest);
                if (completionCount > 0)
                {
                    String key = completionCount > 1 ? "ftbquests.quest.misc.completion_count.plural" : "ftbquests.quest.misc.completion_count";
                    list.add(Component.translatable(key, completionCount).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        if (teamData != null && !teamData.canStartTasks(quest))
        {
            Component reason = teamData.getCannotStartReason(this.quest);
            list.add(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY).append(reason).append("]"));
        }
        if (quest.isExclusiveQuest())
        {
            list.add(Component.translatable("ftbquests.quest.misc.exclusive").withStyle(ChatFormatting.GOLD));
            list.add(Component.translatable("ftbquests.quest.misc.exclusive.desc").withColor(0xFFC08000));
        }
    }
}
