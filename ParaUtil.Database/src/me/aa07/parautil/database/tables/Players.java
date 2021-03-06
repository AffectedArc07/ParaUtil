/*
 * This file is generated by jOOQ.
 */
package me.aa07.parautil.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import me.aa07.parautil.database.Indexes;
import me.aa07.parautil.database.Keys;
import me.aa07.parautil.database.ParadiseMc;
import me.aa07.parautil.database.tables.records.PlayersRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Players extends TableImpl<PlayersRecord> {

    private static final long serialVersionUID = 1486311086;

    /**
     * The reference instance of <code>paradise_mc.players</code>
     */
    public static final Players PLAYERS = new Players();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PlayersRecord> getRecordType() {
        return PlayersRecord.class;
    }

    /**
     * The column <code>paradise_mc.players.uuid</code>.
     */
    public final TableField<PlayersRecord, String> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>paradise_mc.players.last_username</code>.
     */
    public final TableField<PlayersRecord, String> LAST_USERNAME = createField("last_username", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false), this, "");

    /**
     * The column <code>paradise_mc.players.last_ckey</code>.
     */
    public final TableField<PlayersRecord, String> LAST_CKEY = createField("last_ckey", org.jooq.impl.SQLDataType.VARCHAR(32).nullable(false), this, "");

    /**
     * The column <code>paradise_mc.players.last_server</code>.
     */
    public final TableField<PlayersRecord, String> LAST_SERVER = createField("last_server", org.jooq.impl.SQLDataType.VARCHAR(50).defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>paradise_mc.players.first_seen</code>.
     */
    public final TableField<PlayersRecord, Timestamp> FIRST_SEEN = createField("first_seen", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>paradise_mc.players.last_seen</code>.
     */
    public final TableField<PlayersRecord, Timestamp> LAST_SEEN = createField("last_seen", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * Create a <code>paradise_mc.players</code> table reference
     */
    public Players() {
        this(DSL.name("players"), null);
    }

    /**
     * Create an aliased <code>paradise_mc.players</code> table reference
     */
    public Players(String alias) {
        this(DSL.name(alias), PLAYERS);
    }

    /**
     * Create an aliased <code>paradise_mc.players</code> table reference
     */
    public Players(Name alias) {
        this(alias, PLAYERS);
    }

    private Players(Name alias, Table<PlayersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Players(Name alias, Table<PlayersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Players(Table<O> child, ForeignKey<O, PlayersRecord> key) {
        super(child, key, PLAYERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return ParadiseMc.PARADISE_MC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.PLAYERS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PlayersRecord> getPrimaryKey() {
        return Keys.KEY_PLAYERS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PlayersRecord>> getKeys() {
        return Arrays.<UniqueKey<PlayersRecord>>asList(Keys.KEY_PLAYERS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Players as(String alias) {
        return new Players(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Players as(Name alias) {
        return new Players(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Players rename(String name) {
        return new Players(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Players rename(Name name) {
        return new Players(name, null);
    }
}
