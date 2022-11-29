import {Injectable} from '@angular/core';
import {ChangeEvent, ITodo, Todos} from './protos/changeevent';
import {BehaviorSubject} from 'rxjs';
import {environment} from '../environments/environment';
import cettia from 'cettia-client/cettia-bundler';

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  private todos: Map<string, ITodo> = new Map();

  private todosSubject = new BehaviorSubject<ITodo[]>([]);
  public todosObservable = this.todosSubject.asObservable();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private socket: any = null;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private cache: any = [];

  constructor() {
    this.socket = cettia.open(environment.SERVER_URL);

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.socket.on('initial', (msg: any) => {
      const initialTodos = Todos.decode(msg);
      for (const todo of initialTodos.todos) {
        if (todo.id) {
          this.todos.set(todo.id, todo);
        }
      }
      this.todosSubject.next([...this.todos.values()]);
    });

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.socket.on('update', (msg: any) => {
      const changeEvent = ChangeEvent.decode(msg);
      const todo = changeEvent.todo;
      if (todo?.id) {
        if (changeEvent.change === ChangeEvent.ChangeType.DELETE) {
          this.todos.delete(todo.id);
        } else if (changeEvent.change === ChangeEvent.ChangeType.INSERT) {
          this.todos.set(todo.id, todo);
        } else if (changeEvent.change === ChangeEvent.ChangeType.UPDATE) {
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

  getTodo(id: string): ITodo | undefined {
    return this.todos.get(id);
  }

  deleteTodo(todo: ITodo): void {
    if (todo.id) {
      const deleted = this.todos.delete(todo.id);
      if (deleted) {
        this.todosSubject.next([...this.todos.values()]);

        const changeEvent = ChangeEvent.create({change: ChangeEvent.ChangeType.DELETE, todo});
        const buffer = ChangeEvent.encode(changeEvent).finish();
        this.socket.send('update', buffer);
      }
    }
  }

  save(todo: ITodo): void {
    let changeType;
    if (todo.id) {
      if (this.todos.has(todo.id)) {
        changeType = ChangeEvent.ChangeType.UPDATE;
      } else {
        changeType = ChangeEvent.ChangeType.INSERT;
      }

      const changeEvent = ChangeEvent.create({change: changeType, todo});
      const buffer = ChangeEvent.encode(changeEvent).finish();
      this.socket.send('update', buffer);

      this.todos.set(todo.id, todo);
      this.todosSubject.next([...this.todos.values()]);
    }
  }

}
