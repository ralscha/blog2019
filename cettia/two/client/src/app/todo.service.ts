import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {environment} from '../environments/environment';
import cettia from 'cettia-client/cettia-bundler';
import {ChangeEvent, ChangeType, decodeChangeEvent, decodeTodos, encodeChangeEvent, Todo} from "./protos/changeevent";

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  private todos: Map<string, Todo> = new Map();

  private todosSubject = new BehaviorSubject<Todo[]>([]);
  public todosObservable = this.todosSubject.asObservable();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private socket: any = null;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private cache: any = [];

  constructor() {
    this.socket = cettia.open(environment.SERVER_URL);

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.socket.on('initial', (msg: any) => {
      const initialTodos = decodeTodos(msg);
      if (!initialTodos.todos) {
        return;
      }
      for (const todo of initialTodos.todos) {
        if (todo.id) {
          this.todos.set(todo.id, todo);
        }
      }
      this.todosSubject.next([...this.todos.values()]);
    });

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.socket.on('update', (msg: any) => {
      const changeEvent = decodeChangeEvent(msg);
      const todo = changeEvent.todo;
      if (todo?.id) {
        if (changeEvent.change === ChangeType.DELETE) {
          this.todos.delete(todo.id);
        } else if (changeEvent.change === ChangeType.INSERT) {
          this.todos.set(todo.id, todo);
        } else if (changeEvent.change === ChangeType.UPDATE) {
          this.todos.set(todo.id, todo);
        }
        this.todosSubject.next([...this.todos.values()]);
      }
    });

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.socket.on('cache', (args: any) => this.cache.push(args));

    this.socket.on('open', () => {
      while (this.socket.state() === 'opened' && this.cache.length > 0) {
        const args = this.cache.shift();
        this.socket.send(...args);
      }
    });

  }

  getTodo(id: string): Todo | undefined {
    return this.todos.get(id);
  }

  deleteTodo(todo: Todo): void {
    if (todo.id) {
      const deleted = this.todos.delete(todo.id);
      if (deleted) {
        this.todosSubject.next([...this.todos.values()]);

        const changeEvent: ChangeEvent = {change: ChangeType.DELETE, todo};
        const buffer = encodeChangeEvent(changeEvent);
        this.socket.send('update', buffer);
      }
    }
  }

  save(todo: Todo): void {
    let changeType;
    if (todo.id) {
      if (this.todos.has(todo.id)) {
        changeType = ChangeType.UPDATE;
      } else {
        changeType = ChangeType.INSERT;
      }

      const changeEvent: ChangeEvent = {change: changeType, todo};
      const buffer = encodeChangeEvent(changeEvent);
      this.socket.send('update', buffer);

      this.todos.set(todo.id, todo);
      this.todosSubject.next([...this.todos.values()]);
    }
  }

}
