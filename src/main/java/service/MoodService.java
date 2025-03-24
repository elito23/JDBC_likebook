package service;

import model.Mood;
import model.MoodNameEnum;
import repositories.MoodRepository;

import java.sql.SQLException;
import java.util.List;

public class MoodService {
    public final MoodRepository moodRepository;

    public MoodService(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }
    public void createMood(Mood mood) throws SQLException {
        if(mood.getDescription()==null || mood.getDescription().isEmpty()){
            throw new IllegalArgumentException("Mood description cannot be null!");
        }else if(mood.getName()==null){
            throw new IllegalArgumentException("You must specify a mood name!");
        }
        moodRepository.saveMood(mood);
        System.out.println("Mood created successfully");
    }
    public Mood getMoodById(Long id) throws SQLException {
        return moodRepository.findById(id)
                .orElseThrow(()->new SQLException("Mood with id "+id+" not found!"));
    }
    public Mood getMoodByName(MoodNameEnum moodNameEnum) throws SQLException {
        return moodRepository.findByName(moodNameEnum)
                .orElseThrow(()->new SQLException("Mood with name "+moodNameEnum.name()+" not found!"));
    }
    public List<Mood> getAllMoods() throws SQLException {
        return moodRepository.findAll();

    }
    public void deleteById(Long id) throws SQLException {
        moodRepository.deleteById(id);
        System.out.println("Deleted successfully!");
    }
}
