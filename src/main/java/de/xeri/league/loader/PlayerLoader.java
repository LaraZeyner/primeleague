package de.xeri.league.loader;

import java.util.stream.Stream;

import de.xeri.league.models.league.Account;
import de.xeri.league.util.io.riot.RiotAccountRequester;

/**
 * Created by Lara on 07.04.2022 for web
 */
public final class PlayerLoader {
  static {
    final Stream<Account> accountStream = Account.get().stream();
    accountStream.filter(Account::isValueable).filter(Account::isActive).forEach(RiotAccountRequester::loadElo);
    accountStream.filter(Account::isValueable).filter(Account::isActive).forEach(RiotAccountRequester::loadAll);
    accountStream.filter(Account::isActive).forEach(RiotAccountRequester::loadCompetitive);
  }

  public static void load() {

  }

}
