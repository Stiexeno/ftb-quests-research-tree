package dev.ftb.mods.ftbquests.client.gui.quests.chapter;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel;

public class ResearchChapterPanelView extends BaseChapterPanelView
{
    public ResearchChapterPanelView(ChapterPanel panel)
    {
        super(panel);
    }
    
    @Override
    public void addWidgets(ClientQuestFile file)
    {
        panel.add(new ResearchHeaderButton(panel, file));
        panel.add(new ResearchGrid(panel, file.getDefaultChapterGroup().getVisibleChapters(file.selfTeamData).getFirst()));
    }
}
