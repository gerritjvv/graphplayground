package org.blockchain;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

import java.io.IOException;
import java.util.Optional;

/**
 * A test app that shows how to listen to new blocks and transform
 * their transaction hashes into Transaction objects.
 *
 */
public class App {


    public static final void main(String[] args) throws Throwable {

        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/

        Subscription subscription = web3.blockObservable(false)
                .flatMapIterable(block -> block.getBlock().getTransactions())
                .<Optional<Transaction>>map(tx -> {
                    try {
                        return web3.ethGetTransactionByHash(tx.get().toString()).send().getTransaction();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribe((tx) -> {
                    System.out.println("Transaction [" + tx.getFrom() + " => " +  tx.getTo() + "]");
                });

        // or we could listen to transactions only:
//        web3.transactionObservable()
//                .subscribe(tx -> {
//                    System.out.println("Transaction [" + tx.getFrom() + " => " +  tx.getTo() + "]");
//                });
//
        //subscription can be used to cancel subscription

        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(5000);
            System.out.println("Watiting");
        }
    }

}
