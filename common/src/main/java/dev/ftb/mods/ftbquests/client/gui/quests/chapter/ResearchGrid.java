package dev.ftb.mods.ftbquests.client.gui.quests.chapter;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel;
import dev.ftb.mods.ftbquests.quest.Chapter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ResearchGrid extends ChapterPanel.ListButton
{
    private final Chapter chapter;
    private final ChapterPanel chapterPanel;
    private final List<ResearchQuestButton> widgets;
    
    private final int columns = 7;
    
    public ResearchGrid(ChapterPanel panel, Chapter c)
    {
        super(panel, c.getTitle(), c.getIcon());
        
        chapter = c;
        chapterPanel = panel;
        
        widgets = chapter.getQuests().stream()
            .map(quest -> new ResearchQuestButton(chapterPanel, chapter, quest))
            .toList();
    }
    
    public void refreshHeight()
    {
        int iconSize = getWidth() / columns;
        int rows = (int) Math.ceil((double) widgets.size() / columns);
        setHeight(rows * iconSize);
    }
    
    @Override
    public void updateMouseOver(int mouseX, int mouseY) {
        super.updateMouseOver(mouseX, mouseY);
        
        for (var widget : widgets) {
            widget.updateMouseOver(mouseX, mouseY);
        }
    }
    
    @Override
    public boolean mousePressed(MouseButton button) {
        for (var widget : widgets) {
            if (widget.mousePressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void addMouseOverText(TooltipList list) {
        if (!shouldAddMouseOverText() || !isMouseOver()) {
            return;
        }
        
        for (var i = widgets.size() - 1; i >= 0; i--) {
            var widget = widgets.get(i);
            
            if (widget.shouldAddMouseOverText()) {
                widget.addMouseOverText(list);
                
                if (Theme.renderDebugBoxes) {
                    list.styledString(widget + "#" + (i + 1) + ": " + widget.width + "x" + widget.height, ChatFormatting.DARK_GRAY);
                }
            }
        }
    }
    
    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h)
    {
        GuiHelper.setupDrawing();
        
        //drawBackground(graphics, theme, x, y, w, h);
        
        int iconSize = getWidth() / columns;
        int rows = (int) Math.ceil((double) widgets.size() / columns);
        
        x += 1;
        y -= 1;
        
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < columns; col++)
            {
                int questIndex = row * columns + col;
                if (questIndex >= widgets.size()) break;
                
                int iconX = x + col * iconSize;
                int iconY = y + row * iconSize;
                
                widgets.get(questIndex).draw(graphics, theme, iconX, iconY, iconSize, iconSize);
                widgets.get(questIndex).setPosAndSize(iconX, iconY, iconSize, iconSize);
            }
        }
    }
    
    @Override
    public void onClicked(MouseButton button)
    {
    }
}

