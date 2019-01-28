/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
(function (global, factory) { /* global define, require, module */

  /* AMD */
  if (typeof define === 'function' && define.amd)
    define(["protobufjs/minimal"], factory);

  /* CommonJS */ else if (typeof require === 'function' && typeof module === 'object' && module && module.exports)
    module.exports = factory(require("protobufjs/minimal"));

})(this, function ($protobuf) {
  "use strict";

  // Common aliases
  var $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

  // Exported root namespace
  var $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

  $root.Todo = (function () {

    /**
     * Properties of a Todo.
     * @exports ITodo
     * @interface ITodo
     * @property {string|null} [id] Todo id
     * @property {string|null} [title] Todo title
     * @property {boolean|null} [completed] Todo completed
     */

    /**
     * Constructs a new Todo.
     * @exports Todo
     * @classdesc Represents a Todo.
     * @implements ITodo
     * @constructor
     * @param {ITodo=} [properties] Properties to set
     */
    function Todo(properties) {
      if (properties)
        for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
          if (properties[keys[i]] != null)
            this[keys[i]] = properties[keys[i]];
    }

    /**
     * Todo id.
     * @member {string} id
     * @memberof Todo
     * @instance
     */
    Todo.prototype.id = "";

    /**
     * Todo title.
     * @member {string} title
     * @memberof Todo
     * @instance
     */
    Todo.prototype.title = "";

    /**
     * Todo completed.
     * @member {boolean} completed
     * @memberof Todo
     * @instance
     */
    Todo.prototype.completed = false;

    /**
     * Creates a new Todo instance using the specified properties.
     * @function create
     * @memberof Todo
     * @static
     * @param {ITodo=} [properties] Properties to set
     * @returns {Todo} Todo instance
     */
    Todo.create = function create(properties) {
      return new Todo(properties);
    };

    /**
     * Encodes the specified Todo message. Does not implicitly {@link Todo.verify|verify} messages.
     * @function encode
     * @memberof Todo
     * @static
     * @param {ITodo} message Todo message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Todo.encode = function encode(message, writer) {
      if (!writer)
        writer = $Writer.create();
      if (message.id != null && message.hasOwnProperty("id"))
        writer.uint32(/* id 1, wireType 2 =*/10).string(message.id);
      if (message.title != null && message.hasOwnProperty("title"))
        writer.uint32(/* id 2, wireType 2 =*/18).string(message.title);
      if (message.completed != null && message.hasOwnProperty("completed"))
        writer.uint32(/* id 3, wireType 0 =*/24).bool(message.completed);
      return writer;
    };

    /**
     * Encodes the specified Todo message, length delimited. Does not implicitly {@link Todo.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Todo
     * @static
     * @param {ITodo} message Todo message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Todo.encodeDelimited = function encodeDelimited(message, writer) {
      return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Todo message from the specified reader or buffer.
     * @function decode
     * @memberof Todo
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Todo} Todo
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Todo.decode = function decode(reader, length) {
      if (!(reader instanceof $Reader))
        reader = $Reader.create(reader);
      var end = length === undefined ? reader.len : reader.pos + length, message = new $root.Todo();
      while (reader.pos < end) {
        var tag = reader.uint32();
        switch (tag >>> 3) {
          case 1:
            message.id = reader.string();
            break;
          case 2:
            message.title = reader.string();
            break;
          case 3:
            message.completed = reader.bool();
            break;
          default:
            reader.skipType(tag & 7);
            break;
        }
      }
      return message;
    };

    /**
     * Decodes a Todo message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Todo
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Todo} Todo
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Todo.decodeDelimited = function decodeDelimited(reader) {
      if (!(reader instanceof $Reader))
        reader = new $Reader(reader);
      return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Todo message.
     * @function verify
     * @memberof Todo
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Todo.verify = function verify(message) {
      if (typeof message !== "object" || message === null)
        return "object expected";
      if (message.id != null && message.hasOwnProperty("id"))
        if (!$util.isString(message.id))
          return "id: string expected";
      if (message.title != null && message.hasOwnProperty("title"))
        if (!$util.isString(message.title))
          return "title: string expected";
      if (message.completed != null && message.hasOwnProperty("completed"))
        if (typeof message.completed !== "boolean")
          return "completed: boolean expected";
      return null;
    };

    /**
     * Creates a Todo message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Todo
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Todo} Todo
     */
    Todo.fromObject = function fromObject(object) {
      if (object instanceof $root.Todo)
        return object;
      var message = new $root.Todo();
      if (object.id != null)
        message.id = String(object.id);
      if (object.title != null)
        message.title = String(object.title);
      if (object.completed != null)
        message.completed = Boolean(object.completed);
      return message;
    };

    /**
     * Creates a plain object from a Todo message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Todo
     * @static
     * @param {Todo} message Todo
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Todo.toObject = function toObject(message, options) {
      if (!options)
        options = {};
      var object = {};
      if (options.defaults) {
        object.id = "";
        object.title = "";
        object.completed = false;
      }
      if (message.id != null && message.hasOwnProperty("id"))
        object.id = message.id;
      if (message.title != null && message.hasOwnProperty("title"))
        object.title = message.title;
      if (message.completed != null && message.hasOwnProperty("completed"))
        object.completed = message.completed;
      return object;
    };

    /**
     * Converts this Todo to JSON.
     * @function toJSON
     * @memberof Todo
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Todo.prototype.toJSON = function toJSON() {
      return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Todo;
  })();

  $root.Todos = (function () {

    /**
     * Properties of a Todos.
     * @exports ITodos
     * @interface ITodos
     * @property {Array.<ITodo>|null} [todos] Todos todos
     */

    /**
     * Constructs a new Todos.
     * @exports Todos
     * @classdesc Represents a Todos.
     * @implements ITodos
     * @constructor
     * @param {ITodos=} [properties] Properties to set
     */
    function Todos(properties) {
      this.todos = [];
      if (properties)
        for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
          if (properties[keys[i]] != null)
            this[keys[i]] = properties[keys[i]];
    }

    /**
     * Todos todos.
     * @member {Array.<ITodo>} todos
     * @memberof Todos
     * @instance
     */
    Todos.prototype.todos = $util.emptyArray;

    /**
     * Creates a new Todos instance using the specified properties.
     * @function create
     * @memberof Todos
     * @static
     * @param {ITodos=} [properties] Properties to set
     * @returns {Todos} Todos instance
     */
    Todos.create = function create(properties) {
      return new Todos(properties);
    };

    /**
     * Encodes the specified Todos message. Does not implicitly {@link Todos.verify|verify} messages.
     * @function encode
     * @memberof Todos
     * @static
     * @param {ITodos} message Todos message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Todos.encode = function encode(message, writer) {
      if (!writer)
        writer = $Writer.create();
      if (message.todos != null && message.todos.length)
        for (var i = 0; i < message.todos.length; ++i)
          $root.Todo.encode(message.todos[i], writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
      return writer;
    };

    /**
     * Encodes the specified Todos message, length delimited. Does not implicitly {@link Todos.verify|verify} messages.
     * @function encodeDelimited
     * @memberof Todos
     * @static
     * @param {ITodos} message Todos message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    Todos.encodeDelimited = function encodeDelimited(message, writer) {
      return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a Todos message from the specified reader or buffer.
     * @function decode
     * @memberof Todos
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {Todos} Todos
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Todos.decode = function decode(reader, length) {
      if (!(reader instanceof $Reader))
        reader = $Reader.create(reader);
      var end = length === undefined ? reader.len : reader.pos + length, message = new $root.Todos();
      while (reader.pos < end) {
        var tag = reader.uint32();
        switch (tag >>> 3) {
          case 1:
            if (!(message.todos && message.todos.length))
              message.todos = [];
            message.todos.push($root.Todo.decode(reader, reader.uint32()));
            break;
          default:
            reader.skipType(tag & 7);
            break;
        }
      }
      return message;
    };

    /**
     * Decodes a Todos message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof Todos
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {Todos} Todos
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    Todos.decodeDelimited = function decodeDelimited(reader) {
      if (!(reader instanceof $Reader))
        reader = new $Reader(reader);
      return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a Todos message.
     * @function verify
     * @memberof Todos
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    Todos.verify = function verify(message) {
      if (typeof message !== "object" || message === null)
        return "object expected";
      if (message.todos != null && message.hasOwnProperty("todos")) {
        if (!Array.isArray(message.todos))
          return "todos: array expected";
        for (var i = 0; i < message.todos.length; ++i) {
          var error = $root.Todo.verify(message.todos[i]);
          if (error)
            return "todos." + error;
        }
      }
      return null;
    };

    /**
     * Creates a Todos message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof Todos
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {Todos} Todos
     */
    Todos.fromObject = function fromObject(object) {
      if (object instanceof $root.Todos)
        return object;
      var message = new $root.Todos();
      if (object.todos) {
        if (!Array.isArray(object.todos))
          throw TypeError(".Todos.todos: array expected");
        message.todos = [];
        for (var i = 0; i < object.todos.length; ++i) {
          if (typeof object.todos[i] !== "object")
            throw TypeError(".Todos.todos: object expected");
          message.todos[i] = $root.Todo.fromObject(object.todos[i]);
        }
      }
      return message;
    };

    /**
     * Creates a plain object from a Todos message. Also converts values to other types if specified.
     * @function toObject
     * @memberof Todos
     * @static
     * @param {Todos} message Todos
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    Todos.toObject = function toObject(message, options) {
      if (!options)
        options = {};
      var object = {};
      if (options.arrays || options.defaults)
        object.todos = [];
      if (message.todos && message.todos.length) {
        object.todos = [];
        for (var j = 0; j < message.todos.length; ++j)
          object.todos[j] = $root.Todo.toObject(message.todos[j], options);
      }
      return object;
    };

    /**
     * Converts this Todos to JSON.
     * @function toJSON
     * @memberof Todos
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    Todos.prototype.toJSON = function toJSON() {
      return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    return Todos;
  })();

  $root.ChangeEvent = (function () {

    /**
     * Properties of a ChangeEvent.
     * @exports IChangeEvent
     * @interface IChangeEvent
     * @property {ITodo|null} [todo] ChangeEvent todo
     * @property {ChangeEvent.ChangeType|null} [change] ChangeEvent change
     */

    /**
     * Constructs a new ChangeEvent.
     * @exports ChangeEvent
     * @classdesc Represents a ChangeEvent.
     * @implements IChangeEvent
     * @constructor
     * @param {IChangeEvent=} [properties] Properties to set
     */
    function ChangeEvent(properties) {
      if (properties)
        for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
          if (properties[keys[i]] != null)
            this[keys[i]] = properties[keys[i]];
    }

    /**
     * ChangeEvent todo.
     * @member {ITodo|null|undefined} todo
     * @memberof ChangeEvent
     * @instance
     */
    ChangeEvent.prototype.todo = null;

    /**
     * ChangeEvent change.
     * @member {ChangeEvent.ChangeType} change
     * @memberof ChangeEvent
     * @instance
     */
    ChangeEvent.prototype.change = 0;

    /**
     * Creates a new ChangeEvent instance using the specified properties.
     * @function create
     * @memberof ChangeEvent
     * @static
     * @param {IChangeEvent=} [properties] Properties to set
     * @returns {ChangeEvent} ChangeEvent instance
     */
    ChangeEvent.create = function create(properties) {
      return new ChangeEvent(properties);
    };

    /**
     * Encodes the specified ChangeEvent message. Does not implicitly {@link ChangeEvent.verify|verify} messages.
     * @function encode
     * @memberof ChangeEvent
     * @static
     * @param {IChangeEvent} message ChangeEvent message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    ChangeEvent.encode = function encode(message, writer) {
      if (!writer)
        writer = $Writer.create();
      if (message.todo != null && message.hasOwnProperty("todo"))
        $root.Todo.encode(message.todo, writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
      if (message.change != null && message.hasOwnProperty("change"))
        writer.uint32(/* id 2, wireType 0 =*/16).int32(message.change);
      return writer;
    };

    /**
     * Encodes the specified ChangeEvent message, length delimited. Does not implicitly {@link ChangeEvent.verify|verify} messages.
     * @function encodeDelimited
     * @memberof ChangeEvent
     * @static
     * @param {IChangeEvent} message ChangeEvent message or plain object to encode
     * @param {$protobuf.Writer} [writer] Writer to encode to
     * @returns {$protobuf.Writer} Writer
     */
    ChangeEvent.encodeDelimited = function encodeDelimited(message, writer) {
      return this.encode(message, writer).ldelim();
    };

    /**
     * Decodes a ChangeEvent message from the specified reader or buffer.
     * @function decode
     * @memberof ChangeEvent
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @param {number} [length] Message length if known beforehand
     * @returns {ChangeEvent} ChangeEvent
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    ChangeEvent.decode = function decode(reader, length) {
      if (!(reader instanceof $Reader))
        reader = $Reader.create(reader);
      var end = length === undefined ? reader.len : reader.pos + length, message = new $root.ChangeEvent();
      while (reader.pos < end) {
        var tag = reader.uint32();
        switch (tag >>> 3) {
          case 1:
            message.todo = $root.Todo.decode(reader, reader.uint32());
            break;
          case 2:
            message.change = reader.int32();
            break;
          default:
            reader.skipType(tag & 7);
            break;
        }
      }
      return message;
    };

    /**
     * Decodes a ChangeEvent message from the specified reader or buffer, length delimited.
     * @function decodeDelimited
     * @memberof ChangeEvent
     * @static
     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
     * @returns {ChangeEvent} ChangeEvent
     * @throws {Error} If the payload is not a reader or valid buffer
     * @throws {$protobuf.util.ProtocolError} If required fields are missing
     */
    ChangeEvent.decodeDelimited = function decodeDelimited(reader) {
      if (!(reader instanceof $Reader))
        reader = new $Reader(reader);
      return this.decode(reader, reader.uint32());
    };

    /**
     * Verifies a ChangeEvent message.
     * @function verify
     * @memberof ChangeEvent
     * @static
     * @param {Object.<string,*>} message Plain object to verify
     * @returns {string|null} `null` if valid, otherwise the reason why it is not
     */
    ChangeEvent.verify = function verify(message) {
      if (typeof message !== "object" || message === null)
        return "object expected";
      if (message.todo != null && message.hasOwnProperty("todo")) {
        var error = $root.Todo.verify(message.todo);
        if (error)
          return "todo." + error;
      }
      if (message.change != null && message.hasOwnProperty("change"))
        switch (message.change) {
          default:
            return "change: enum value expected";
          case 0:
          case 1:
          case 2:
            break;
        }
      return null;
    };

    /**
     * Creates a ChangeEvent message from a plain object. Also converts values to their respective internal types.
     * @function fromObject
     * @memberof ChangeEvent
     * @static
     * @param {Object.<string,*>} object Plain object
     * @returns {ChangeEvent} ChangeEvent
     */
    ChangeEvent.fromObject = function fromObject(object) {
      if (object instanceof $root.ChangeEvent)
        return object;
      var message = new $root.ChangeEvent();
      if (object.todo != null) {
        if (typeof object.todo !== "object")
          throw TypeError(".ChangeEvent.todo: object expected");
        message.todo = $root.Todo.fromObject(object.todo);
      }
      switch (object.change) {
        case "INSERT":
        case 0:
          message.change = 0;
          break;
        case "UPDATE":
        case 1:
          message.change = 1;
          break;
        case "DELETE":
        case 2:
          message.change = 2;
          break;
      }
      return message;
    };

    /**
     * Creates a plain object from a ChangeEvent message. Also converts values to other types if specified.
     * @function toObject
     * @memberof ChangeEvent
     * @static
     * @param {ChangeEvent} message ChangeEvent
     * @param {$protobuf.IConversionOptions} [options] Conversion options
     * @returns {Object.<string,*>} Plain object
     */
    ChangeEvent.toObject = function toObject(message, options) {
      if (!options)
        options = {};
      var object = {};
      if (options.defaults) {
        object.todo = null;
        object.change = options.enums === String ? "INSERT" : 0;
      }
      if (message.todo != null && message.hasOwnProperty("todo"))
        object.todo = $root.Todo.toObject(message.todo, options);
      if (message.change != null && message.hasOwnProperty("change"))
        object.change = options.enums === String ? $root.ChangeEvent.ChangeType[message.change] : message.change;
      return object;
    };

    /**
     * Converts this ChangeEvent to JSON.
     * @function toJSON
     * @memberof ChangeEvent
     * @instance
     * @returns {Object.<string,*>} JSON object
     */
    ChangeEvent.prototype.toJSON = function toJSON() {
      return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
    };

    /**
     * ChangeType enum.
     * @name ChangeEvent.ChangeType
     * @enum {string}
     * @property {number} INSERT=0 INSERT value
     * @property {number} UPDATE=1 UPDATE value
     * @property {number} DELETE=2 DELETE value
     */
    ChangeEvent.ChangeType = (function () {
      var valuesById = {}, values = Object.create(valuesById);
      values[valuesById[0] = "INSERT"] = 0;
      values[valuesById[1] = "UPDATE"] = 1;
      values[valuesById[2] = "DELETE"] = 2;
      return values;
    })();

    return ChangeEvent;
  })();

  return $root;
});
