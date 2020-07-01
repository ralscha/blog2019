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

  private todosSubject = new BehaviorSubject([]);
  public todosObservable = this.todosSubject.asObservable();

  private socket: any = null;
  private cache: any = [];

  constructor() {
    this.socket = cettia.open(environment.SERVER_URL);

    this.socket.on('initial', msg => {
      const initialTodos = Todos.decode(msg);
      for (const todo of initialTodos.todos) {
        this.todos.set(todo.id, todo);
      }
      this.todosSubject.next([...this.todos.values()]);
    });

    this.socket.on('update', msg => {
      const changeEvent = ChangeEvent.decode(msg);
      const todo = changeEvent.todo;

      if (changeEvent.change === ChangeEvent.ChangeType.DELETE) {
        this.todos.delete(todo.id);
      } else if (changeEvent.change === ChangeEvent.ChangeType.INSERT) {
        this.todos.set(todo.id, todo);
      } else if (changeEvent.change === ChangeEvent.ChangeType.UPDATE) {
        this.todos.set(todo.id, todo);
      }

      this.todosSubject.next([...this.todos.values()]);
    });

    this.socket.on('cache', args => this.cache.push(args));

    this.socket.on('open', () => {
      while (this.socket.state() === 'opened' && this.cache.length > 0) {
        const args = this.cache.shift();
        this.socket.send(...args);
      }
    });

  }

  getTodo(id: string): ITodo {
    return this.todos.get(id);
  }

  deleteTodo(todo: ITodo) {
    const deleted = this.todos.delete(todo.id);
    if (deleted) {
      this.todosSubject.next([...this.todos.values()]);

      const changeEvent = ChangeEvent.create({change: ChangeEvent.ChangeType.DELETE, todo});
      const buffer = ChangeEvent.encode(changeEvent).finish();
      this.socket.send('update', buffer);
    }
  }

  save(todo: ITodo) {
    let changeType;
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
