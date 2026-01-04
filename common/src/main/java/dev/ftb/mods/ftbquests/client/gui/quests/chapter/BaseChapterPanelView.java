package dev.ftb.mods.ftbquests.client.gui.quests.chapter;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel;

public abstract class BaseChapterPanelView
{
    protected final ChapterPanel panel;
    
    public BaseChapterPanelView(ChapterPanel panel)
    {
        this.panel = panel;
    }
    
    public abstract void addWidgets(ClientQuestFile file);
}

