package dev.ftb.mods.ftbquests.client.gui.quests.chapter;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.quests.ChapterPanel;
import dev.ftb.mods.ftbquests.quest.Chapter;

public class ChapterPanelView extends BaseChapterPanelView
{
    public ChapterPanelView(ChapterPanel panel)
    {
        super(panel);
    }
    
    @Override
    public void addWidgets(ClientQuestFile file)
    {
        panel.clearWidgets();
        
        panel.add(new ChapterPanel.ModpackButton(panel, file));
        
        boolean canEdit = file.canEdit();
        
        for (Chapter chapter : file.getDefaultChapterGroup().getVisibleChapters(file.selfTeamData))
        {
            panel.add(new ChapterPanel.ChapterButton(panel, chapter));
        }
        
        file.forAllChapterGroups(group ->
        {
            if (!group.isDefaultGroup())
            {
                ChapterPanel.ChapterGroupButton button = new ChapterPanel.ChapterGroupButton(panel, group);
                if (canEdit || !button.visibleChapters.isEmpty())
                {
                    panel.add(button);
                    if (!group.isGuiCollapsed())
                    {
                        button.visibleChapters.forEach(chapter -> panel.add(new ChapterPanel.ChapterButton(panel, chapter)));
                    }
                }
            }
        });
    }
}
