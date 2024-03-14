package br.com.itau.account.challenge.kafka.exception;

public class TopicNameEmptyException extends RuntimeException {

    public TopicNameEmptyException(final String message) {
        super(message);
    }

}
