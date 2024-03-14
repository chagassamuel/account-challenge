package br.com.itau.account.challenge.utils;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.springframework.stereotype.Component;



@Component
public class RxJavaUtil {

    public Observable<Integer> getObservableParallel() {
        return Observable.just(1).observeOn(Schedulers.computation());
    }

}
