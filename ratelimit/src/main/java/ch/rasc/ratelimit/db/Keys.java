/*
 * This file is generated by jOOQ.
 */
package ch.rasc.ratelimit.db;


import ch.rasc.ratelimit.db.tables.Earthquake;
import ch.rasc.ratelimit.db.tables.records.EarthquakeRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in the
 * default schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<EarthquakeRecord> CONSTRAINT_9 = Internal.createUniqueKey(Earthquake.EARTHQUAKE, DSL.name("CONSTRAINT_9"), new TableField[] { Earthquake.EARTHQUAKE.ID }, true);
}
