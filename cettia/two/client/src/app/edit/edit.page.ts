import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {NavController} from '@ionic/angular';
import {TodoService} from '../todo.service';
import {v4 as uuid} from 'uuid';
import {ITodo} from '../protos/changeevent';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.page.html',
  styleUrls: ['./edit.page.scss'],
})
export class EditPage implements OnInit {

  todo: ITodo | undefined;

  constructor(private readonly navCtrl: NavController,
              private readonly route: ActivatedRoute,
              private readonly todoService: TodoService) {

  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.todo = this.todoService.getTodo(id);
    } else {
      this.todo = {
        id: uuid(),
        title: '',
        completed: false
      };
    }
  }

  save(): void {
    if (this.todo) {
      this.todoService.save(this.todo);
    }
    this.navCtrl.navigateBack(['home']);
  }

  deleteTodo(): void {
    if (this.todo) {
      this.todoService.deleteTodo(this.todo);
    }
    this.navCtrl.navigateBack(['home']);
  }


}
