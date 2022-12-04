/*
 * This file is generated by jOOQ.
 */
package ch.rasc.stateless.db.tables;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import ch.rasc.stateless.db.DefaultSchema;
import ch.rasc.stateless.db.Keys;
import ch.rasc.stateless.db.tables.records.AppSessionRecord;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppSession extends TableImpl<AppSessionRecord> {

  private static final long serialVersionUID = 1L;

  /**
   * The reference instance of <code>APP_SESSION</code>
   */
  public static final AppSession APP_SESSION = new AppSession();

  /**
   * The class holding records for this type
   */
  @Override
  public Class<AppSessionRecord> getRecordType() {
    return AppSessionRecord.class;
  }

  /**
   * The column <code>APP_SESSION.ID</code>.
   */
  public final TableField<AppSessionRecord, String> ID = createField(DSL.name("ID"),
      SQLDataType.CHAR(35).nullable(false), this, "");

  /**
   * The column <code>APP_SESSION.APP_USER_ID</code>.
   */
  public final TableField<AppSessionRecord, Long> APP_USER_ID = createField(
      DSL.name("APP_USER_ID"), SQLDataType.BIGINT.nullable(false), this, "");

  /**
   * The column <code>APP_SESSION.VALID_UNTIL</code>.
   */
  public final TableField<AppSessionRecord, LocalDateTime> VALID_UNTIL = createField(
      DSL.name("VALID_UNTIL"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(
          DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)),
      this, "");

  private AppSession(Name alias, Table<AppSessionRecord> aliased) {
    this(alias, aliased, null);
  }

  private AppSession(Name alias, Table<AppSessionRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /**
   * Create an aliased <code>APP_SESSION</code> table reference
   */
  public AppSession(String alias) {
    this(DSL.name(alias), APP_SESSION);
  }

  /**
   * Create an aliased <code>APP_SESSION</code> table reference
   */
  public AppSession(Name alias) {
    this(alias, APP_SESSION);
  }

  /**
   * Create a <code>APP_SESSION</code> table reference
   */
  public AppSession() {
    this(DSL.name("APP_SESSION"), null);
  }

  public <O extends Record> AppSession(Table<O> child,
      ForeignKey<O, AppSessionRecord> key) {
    super(child, key, APP_SESSION);
  }

  @Override
  public Schema getSchema() {
    return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
  }

  @Override
  public UniqueKey<AppSessionRecord> getPrimaryKey() {
    return Keys.CONSTRAINT_A;
  }

  @Override
  public List<ForeignKey<AppSessionRecord, ?>> getReferences() {
    return Arrays.asList(Keys.CONSTRAINT_A8);
  }

  private transient AppUser _appUser;

  /**
   * Get the implicit join path to the <code>PUBLIC.APP_USER</code> table.
   */
  public AppUser appUser() {
    if (this._appUser == null) {
      this._appUser = new AppUser(this, Keys.CONSTRAINT_A8);
    }

    return this._appUser;
  }

  @Override
  public AppSession as(String alias) {
    return new AppSession(DSL.name(alias), this);
  }

  @Override
  public AppSession as(Name alias) {
    return new AppSession(alias, this);
  }

  @Override
  public AppSession as(Table<?> alias) {
    return new AppSession(alias.getQualifiedName(), this);
  }

  /**
   * Rename this table
   */
  @Override
  public AppSession rename(String name) {
    return new AppSession(DSL.name(name), null);
  }

  /**
   * Rename this table
   */
  @Override
  public AppSession rename(Name name) {
    return new AppSession(name, null);
  }

  /**
   * Rename this table
   */
  @Override
  public AppSession rename(Table<?> name) {
    return new AppSession(name.getQualifiedName(), null);
  }

  // -------------------------------------------------------------------------
  // Row3 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row3<String, Long, LocalDateTime> fieldsRow() {
    return (Row3) super.fieldsRow();
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
   */
  public <U> SelectField<U> mapping(
      Function3<? super String, ? super Long, ? super LocalDateTime, ? extends U> from) {
    return convertFrom(Records.mapping(from));
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Class, Function)}.
   */
  public <U> SelectField<U> mapping(Class<U> toType,
      Function3<? super String, ? super Long, ? super LocalDateTime, ? extends U> from) {
    return convertFrom(toType, Records.mapping(from));
  }
}
