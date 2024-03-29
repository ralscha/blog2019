/*
 * This file is generated by jOOQ.
 */
package ch.rasc.stateless.db.tables;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function5;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row5;
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
import ch.rasc.stateless.db.tables.records.AppUserRecord;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppUser extends TableImpl<AppUserRecord> {

  private static final long serialVersionUID = 1L;

  /**
   * The reference instance of <code>APP_USER</code>
   */
  public static final AppUser APP_USER = new AppUser();

  /**
   * The class holding records for this type
   */
  @Override
  public Class<AppUserRecord> getRecordType() {
    return AppUserRecord.class;
  }

  /**
   * The column <code>APP_USER.ID</code>.
   */
  public final TableField<AppUserRecord, Long> ID = createField(DSL.name("ID"),
      SQLDataType.BIGINT.nullable(false).identity(true), this, "");

  /**
   * The column <code>APP_USER.EMAIL</code>.
   */
  public final TableField<AppUserRecord, String> EMAIL = createField(DSL.name("EMAIL"),
      SQLDataType.VARCHAR(255).nullable(false), this, "");

  /**
   * The column <code>APP_USER.PASSWORD_HASH</code>.
   */
  public final TableField<AppUserRecord, String> PASSWORD_HASH = createField(
      DSL.name("PASSWORD_HASH"), SQLDataType.VARCHAR(255), this, "");

  /**
   * The column <code>APP_USER.AUTHORITY</code>.
   */
  public final TableField<AppUserRecord, String> AUTHORITY = createField(
      DSL.name("AUTHORITY"), SQLDataType.VARCHAR(255), this, "");

  /**
   * The column <code>APP_USER.ENABLED</code>.
   */
  public final TableField<AppUserRecord, Boolean> ENABLED = createField(
      DSL.name("ENABLED"), SQLDataType.BOOLEAN.nullable(false), this, "");

  private AppUser(Name alias, Table<AppUserRecord> aliased) {
    this(alias, aliased, null);
  }

  private AppUser(Name alias, Table<AppUserRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /**
   * Create an aliased <code>APP_USER</code> table reference
   */
  public AppUser(String alias) {
    this(DSL.name(alias), APP_USER);
  }

  /**
   * Create an aliased <code>APP_USER</code> table reference
   */
  public AppUser(Name alias) {
    this(alias, APP_USER);
  }

  /**
   * Create a <code>APP_USER</code> table reference
   */
  public AppUser() {
    this(DSL.name("APP_USER"), null);
  }

  public <O extends Record> AppUser(Table<O> child, ForeignKey<O, AppUserRecord> key) {
    super(child, key, APP_USER);
  }

  @Override
  public Schema getSchema() {
    return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
  }

  @Override
  public Identity<AppUserRecord, Long> getIdentity() {
    return (Identity<AppUserRecord, Long>) super.getIdentity();
  }

  @Override
  public UniqueKey<AppUserRecord> getPrimaryKey() {
    return Keys.CONSTRAINT_7;
  }

  @Override
  public List<UniqueKey<AppUserRecord>> getUniqueKeys() {
    return Arrays.asList(Keys.CONSTRAINT_76);
  }

  @Override
  public AppUser as(String alias) {
    return new AppUser(DSL.name(alias), this);
  }

  @Override
  public AppUser as(Name alias) {
    return new AppUser(alias, this);
  }

  @Override
  public AppUser as(Table<?> alias) {
    return new AppUser(alias.getQualifiedName(), this);
  }

  /**
   * Rename this table
   */
  @Override
  public AppUser rename(String name) {
    return new AppUser(DSL.name(name), null);
  }

  /**
   * Rename this table
   */
  @Override
  public AppUser rename(Name name) {
    return new AppUser(name, null);
  }

  /**
   * Rename this table
   */
  @Override
  public AppUser rename(Table<?> name) {
    return new AppUser(name.getQualifiedName(), null);
  }

  // -------------------------------------------------------------------------
  // Row5 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row5<Long, String, String, String, Boolean> fieldsRow() {
    return (Row5) super.fieldsRow();
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
   */
  public <U> SelectField<U> mapping(
      Function5<? super Long, ? super String, ? super String, ? super String, ? super Boolean, ? extends U> from) {
    return convertFrom(Records.mapping(from));
  }

  /**
   * Convenience mapping calling {@link SelectField#convertFrom(Class, Function)}.
   */
  public <U> SelectField<U> mapping(Class<U> toType,
      Function5<? super Long, ? super String, ? super String, ? super String, ? super Boolean, ? extends U> from) {
    return convertFrom(toType, Records.mapping(from));
  }
}
