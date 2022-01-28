package fr.lajusticiarugliano.jeecardgames.services;

public class APIServiceException extends Exception{
    public APIServiceException(String errorMessage) {
        super(errorMessage);
    }
}
