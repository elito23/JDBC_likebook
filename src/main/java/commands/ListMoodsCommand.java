package commands;

import model.Mood;
import service.MoodService;

import java.sql.SQLException;
import java.util.List;

public class ListMoodsCommand implements Command {
    private final MoodService moodService;

    public ListMoodsCommand(MoodService moodService) {
        this.moodService = moodService;
    }

    @Override
    public String execute(String[] args) {
        try {
            List<Mood> moods = moodService.getAllMoods();
            if (moods.isEmpty()) {
                return "No moods found.";
            }

            StringBuilder result = new StringBuilder("Available Moods:\n");
            for (Mood mood : moods) {
                result.append(formatMood(mood));
            }

            return result.toString();
        } catch (SQLException e) {
            return "Error retrieving moods: " + e.getMessage();
        }
    }

    private String formatMood(Mood mood) {
        return String.format("ID: %d | Name: %s | Description: %s%n",
                mood.getId(),
                mood.getName(),
                mood.getDescription());
    }
}
