package dev.ftb.mods.ftbquests.client.gui.quests.chapter;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.config.ui.EditStringConfigOverlay;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsClientConfig;
import dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.net.CreateObjectMessage;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ChapterGroup;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import dev.ftb.mods.ftbquests.quest.translation.TranslationKey;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel.NON_EMPTY_PAT;

public class ResearchHeaderButton extends ChapterPanel.ListButton
{
    public ResearchHeaderButton(ChapterPanel panel, ClientQuestFile f)
    {
        super(panel, Component.translatable("ftbquests.gui.research_header"), Icons.STAR);
        setSize(100, 18);
    }
    
    @Override
    public void onClicked(MouseButton button)
    {
        if (getMouseX() > getX() + width - 18)
        {
            playClickSound();
            FTBQuestsClientConfig.setChapterPanelPinned(!FTBQuestsClientConfig.CHAPTER_PANEL_PINNED.get());
        }
        else
        {
            ClientQuestFile file = chapterPanel.questScreen.file;
            if (file.canEdit())
            {
                if (getMouseX() > getX() + width - 34)
                {
                    showAddChapterOrGroupDialog(file);
                }
                else if (button.isLeft() && Screen.hasAltDown())
                {
                    file.onEditButtonClicked(chapterPanel.questScreen);
                }
            }
        }
    }
    
    private void showAddChapterOrGroupDialog(ClientQuestFile file)
    {
        playClickSound();
        
        List<ContextMenuItem> contextMenu = new ArrayList<>();
        contextMenu.add(new ContextMenuItem(Component.translatable("ftbquests.chapter"), ThemeProperties.ADD_ICON.get(), b ->
        {
            StringConfig c = new StringConfig(NON_EMPTY_PAT);
            EditStringConfigOverlay<String> overlay = new EditStringConfigOverlay<>(parent.getParent(), c, accepted ->
            {
                chapterPanel.questScreen.openGui();
                
                if (accepted && !c.getValue().isEmpty())
                {
                    Chapter chapter = new Chapter(0L, file, file.getDefaultChapterGroup(), Chapter.titleToID(c.getValue()).orElse(""));
                    CompoundTag extra = Util.make(new CompoundTag(), t -> t.putLong("group", 0L));
                    file.getTranslationManager().addInitialTranslation(extra, file.getLocale(), TranslationKey.TITLE, c.getValue());
                    NetworkManager.sendToServer(CreateObjectMessage.create(chapter, extra));
                }
                
                run();
            }, b.getTitle()).atMousePosition();
            overlay.setWidth(150);
            overlay.setExtraZlevel(QuestScreen.Z_LEVEL + 10);
            getGui().pushModalPanel(overlay);
        }));
        
        contextMenu.add(new ContextMenuItem(Component.translatable("ftbquests.chapter_group"), ThemeProperties.ADD_ICON.get(), b ->
        {
            StringConfig c = new StringConfig(NON_EMPTY_PAT);
            EditStringConfigOverlay<String> overlay = new EditStringConfigOverlay<>(parent.getParent(), c, accepted ->
            {
                chapterPanel.questScreen.openGui();
                
                if (accepted)
                {
                    ChapterGroup group = new ChapterGroup(0L, ClientQuestFile.INSTANCE);
                    CompoundTag extra = Util.make(new CompoundTag(), t -> t.putLong("group", 0L));
                    file.getTranslationManager().addInitialTranslation(extra, file.getLocale(), TranslationKey.TITLE, c.getValue());
                    NetworkManager.sendToServer(CreateObjectMessage.create(group, extra));
                }
            }, b.getTitle()).atMousePosition();
            overlay.setWidth(150);
            overlay.setExtraZlevel(QuestScreen.Z_LEVEL + 10);
            getGui().pushModalPanel(overlay);
        }));
        
        chapterPanel.questScreen.openContextMenu(contextMenu);
    }
    
    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h)
    {
        GuiHelper.setupDrawing();
        
        if (isMouseOver())
        {
            Color4I.WHITE.withAlpha(40).draw(graphics, x + 1, y + 1, w - 2, h - 2);
        }
        
        ChatFormatting f = isMouseOver() ? ChatFormatting.WHITE : ChatFormatting.GRAY;
        
        icon.draw(graphics, x + 2, y + 3, 12, 12);
        theme.drawString(graphics, Component.literal("").append(title).withStyle(f), x + 16, y + 5);
        
        ThemeProperties.WIDGET_BORDER.get(ClientQuestFile.INSTANCE).draw(graphics, x, y + h - 1, w, 1);
        
        boolean canEdit = chapterPanel.questScreen.file.canEdit();
        
        (chapterPanel.isPinned() ? ThemeProperties.PIN_ICON_ON : ThemeProperties.PIN_ICON_OFF).get().draw(graphics, x + w - 16, y + 3, 12, 12);
        
        if (canEdit)
        {
            ThemeProperties.ADD_ICON.get().draw(graphics, x + w - 31, y + 3, 12, 12);
        }
    }
    
    @Override
    public int getActualWidth(QuestScreen screen)
    {
        boolean canEdit = chapterPanel.questScreen.file.canEdit();
        return screen.getTheme().getStringWidth(title) + 36 + (canEdit ? 16 : 0);
    }
    
    @Override
    public void addMouseOverText(TooltipList list)
    {
        chapterPanel.questScreen.addInfoTooltip(list, chapterPanel.questScreen.file);
        
        if (chapterPanel.questScreen.file.canEdit() && getMouseX() > getX() + width - 34)
        {
            list.translate("gui.add");
        }
    }
}
