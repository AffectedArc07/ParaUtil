/*
 * This file is generated by jOOQ.
 */
package me.aa07.parautil.database;


import javax.annotation.Generated;

import me.aa07.parautil.database.tables.LinkTokens;
import me.aa07.parautil.database.tables.LinkedAccounts;
import me.aa07.parautil.database.tables.Players;
import me.aa07.parautil.database.tables.records.LinkTokensRecord;
import me.aa07.parautil.database.tables.records.LinkedAccountsRecord;
import me.aa07.parautil.database.tables.records.PlayersRecord;

import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>paradise_mc</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<LinkedAccountsRecord> KEY_LINKED_ACCOUNTS_PRIMARY = UniqueKeys0.KEY_LINKED_ACCOUNTS_PRIMARY;
    public static final UniqueKey<LinkTokensRecord> KEY_LINK_TOKENS_PRIMARY = UniqueKeys0.KEY_LINK_TOKENS_PRIMARY;
    public static final UniqueKey<PlayersRecord> KEY_PLAYERS_PRIMARY = UniqueKeys0.KEY_PLAYERS_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<LinkedAccountsRecord> KEY_LINKED_ACCOUNTS_PRIMARY = Internal.createUniqueKey(LinkedAccounts.LINKED_ACCOUNTS, "KEY_linked_accounts_PRIMARY", LinkedAccounts.LINKED_ACCOUNTS.UUID);
        public static final UniqueKey<LinkTokensRecord> KEY_LINK_TOKENS_PRIMARY = Internal.createUniqueKey(LinkTokens.LINK_TOKENS, "KEY_link_tokens_PRIMARY", LinkTokens.LINK_TOKENS.UUID);
        public static final UniqueKey<PlayersRecord> KEY_PLAYERS_PRIMARY = Internal.createUniqueKey(Players.PLAYERS, "KEY_players_PRIMARY", Players.PLAYERS.UUID);
    }
}
