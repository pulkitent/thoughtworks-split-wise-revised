package com.tw.splitwise;

import java.util.List;
import java.util.stream.Collectors;

//This class represents bills to settle
public class Bills {
    private final List<Bill> bills;

   public Bills(List<Bill> bills) {
        this.bills = bills;
    }

    public void settle(List<Friend> friends) {
        for (Bill bill : bills) {
            bill.settle();
        }

        findWhoPaysWhom(friends);
    }

    private void findWhoPaysWhom(List<Friend> friends) {
        List<Friend> payers = findPayers(friends);
        List<Friend> receivers = findReceivers(friends);

        sortPayersAndReceivers(payers, receivers);

        Integer payerIndex = 0;
        Integer receiverIndex = 0;

        while (payerIndex < payers.size()) {
            Friend receiver = receivers.get(receiverIndex);
            Double amountToBeReceived = receiver.calculatePaidAndToPayDifference();

            Friend payer = payers.get(payerIndex);
            Double amountCanBePaid = -payer.calculatePaidAndToPayDifference();

            if (amountCanBePaid > amountToBeReceived) {
                payer.settlementAmountWith(receiver, amountToBeReceived);
                receiverIndex++;
            } else if (amountToBeReceived > amountCanBePaid) {
                receiver.receiveAmountFrom(payer, amountCanBePaid);
                payerIndex++;
            } else {
                payer.addSettlementAmount(new SettlementAmount(amountCanBePaid, receiver));
                payerIndex++;
                receiverIndex++;
            }
        }
    }

    private List<Friend> findPayers(List<Friend> friends) {
        return friends.stream().
                filter(friend -> friend.calculatePaidAndToPayDifference() < 0)
                .collect(Collectors.toList());
    }

    private List<Friend> findReceivers(List<Friend> friends) {
        return friends.stream().
                filter(friend -> friend.calculatePaidAndToPayDifference() > 0)
                .collect(Collectors.toList());
    }

    private void sortPayersAndReceivers(List<Friend> payers, List<Friend> receivers) {
        FriendComparator comparator = new FriendComparator();
        payers.sort(comparator);
        receivers.sort(comparator);
    }
}