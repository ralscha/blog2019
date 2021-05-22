/* eslint-disable @typescript-eslint/no-explicit-any */
import * as $protobuf from 'protobufjs';

export interface ITodo {
  id?: (string | null);
  title?: (string | null);
  completed?: (boolean | null);
}

export class Todo implements ITodo {
  public id: string;
  public title: string;
  public completed: boolean;

  constructor(properties?: ITodo);

  public static create(properties?: ITodo): Todo;

  public static encode(message: ITodo, writer?: $protobuf.Writer): $protobuf.Writer;

  public static encodeDelimited(message: ITodo, writer?: $protobuf.Writer): $protobuf.Writer;

  public static decode(reader: ($protobuf.Reader | Uint8Array), length?: number): Todo;

  public static decodeDelimited(reader: ($protobuf.Reader | Uint8Array)): Todo;

  public static verify(message: { [k: string]: any }): (string | null);

  public static fromObject(object: { [k: string]: any }): Todo;

  public static toObject(message: Todo, options?: $protobuf.IConversionOptions): { [k: string]: any };

  public toJSON(): { [k: string]: any };
}

export interface ITodos {
  todos?: (ITodo[] | null);
}

export class Todos implements ITodos {
  public todos: ITodo[];

  constructor(properties?: ITodos);

  public static create(properties?: ITodos): Todos;

  public static encode(message: ITodos, writer?: $protobuf.Writer): $protobuf.Writer;

  public static encodeDelimited(message: ITodos, writer?: $protobuf.Writer): $protobuf.Writer;

  public static decode(reader: ($protobuf.Reader | Uint8Array), length?: number): Todos;

  public static decodeDelimited(reader: ($protobuf.Reader | Uint8Array)): Todos;

  public static verify(message: { [k: string]: any }): (string | null);

  public static fromObject(object: { [k: string]: any }): Todos;

  public static toObject(message: Todos, options?: $protobuf.IConversionOptions): { [k: string]: any };

  public toJSON(): { [k: string]: any };
}

export interface IChangeEvent {
  todo?: (ITodo | null);
  change?: (ChangeEvent.ChangeType | null);
}

export class ChangeEvent implements IChangeEvent {
  public todo?: (ITodo | null);
  public change: ChangeEvent.ChangeType;

  constructor(properties?: IChangeEvent);

  public static create(properties?: IChangeEvent): ChangeEvent;

  public static encode(message: IChangeEvent, writer?: $protobuf.Writer): $protobuf.Writer;

  public static encodeDelimited(message: IChangeEvent, writer?: $protobuf.Writer): $protobuf.Writer;

  public static decode(reader: ($protobuf.Reader | Uint8Array), length?: number): ChangeEvent;

  public static decodeDelimited(reader: ($protobuf.Reader | Uint8Array)): ChangeEvent;

  public static verify(message: { [k: string]: any }): (string | null);

  public static fromObject(object: { [k: string]: any }): ChangeEvent;

  public static toObject(message: ChangeEvent, options?: $protobuf.IConversionOptions): { [k: string]: any };

  public toJSON(): { [k: string]: any };
}

// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace ChangeEvent {

  enum ChangeType {
    INSERT = 0,
    UPDATE = 1,
    DELETE = 2
  }
}
