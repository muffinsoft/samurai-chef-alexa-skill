package com.muffinsoft.alexa.skills.samuraichef.models;

public class UserAchievements {

    // ############## Performance-based Title Criteria ####################

    // Perfect round (no mistakes)
    boolean perfectRound;

    //Perfect day (no mistakes in entire skill)
    boolean perfectDay;

    //Prepare x sushi in one round (act 1)
    int sushiPrepareCounter;

    //Make x juices in one round (act 2)
    int juicePrepareCounter;

    //Eat x meals in one round (act 3)
    int mealEatInOneRoundCounter;

    //Beat Ben x times (act 3)
    int beatBeanTimesCounter;

    //Break x boards in one round (act 4)
    int boardBreakInOneRoundCounter;

    //Earn x perfect rounds
    int perfectRoundCounter;

    //Play x perfect rounds (not in a row)
    int perfectRoundPlayCounter;

    //Beat your own score for the first time
    boolean beatOwnScourFirstTime;

    //Beat your own score for the nth time
    boolean beatOwnScourNextTime;

    //Have x perfect games
    boolean havePerfectGames;

    //Accumulate x yen in one game
    boolean accumulateYenInOneGame;

    //Accumulate x yen in one round
    boolean accumulateYenInOneRound;

    //Win x games (high number, multiple plays)
    int winGamesCounter;

    //Win x rounds (high number, multiple plays)
    int winRoundsCounter;

    // ############## Loyalty-based Title Criteria ####################

    //Play x rounds (high number)
    int playRoundsCounter;

    //Play x games ( entire skill, high number)
    int playGamesCounter;

    //Prepare or eat x ingredients/meals total (across playtimes)
    int preparedMealsCounter;

    //Play x days in a row
    int daysInARowCounter;

    //Play x rounds in one day
    int roundPerDayCounter;

    //Unlock x pieces of different gear
    int piecesUnlockedCounter;

    //Spend n minutes playing Samurai Sushi (high #)
    int timeSpendCounter;


}
