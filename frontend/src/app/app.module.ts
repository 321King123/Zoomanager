import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { EmployeeComponent } from './components/employee/employee.component';
import { AnimalComponent } from './components/animal/animal.component';
import { AnimalListComponent } from './components/animal-list/animal-list.component';
import { EmployeeViewComponent } from './components/employee-view/employee-view.component';
import { EnclosureComponent } from './components/enclosure/enclosure.component';
import { EnclosureListComponent } from './components/enclosure-list/enclosure-list.component';
import { EnclosureViewComponent } from './components/enclosure-view/enclosure-view.component';
import { TaskCreationComponent } from './components/task-creation/task-creation.component';
import { AnimalViewComponent } from './components/animal-view/animal-view.component';
import { TaskListComponent } from './components/task-list/task-list.component';
import { AssignTaskComponent } from './components/assign-task/assign-task.component';
import { TaskListCommonComponent } from './components/task-list-common/task-list-common.component';
import { AlertComponent } from './components/alert/alert.component';
import { TaskInfoUpdateComponent } from './components/task-info-update/task-info-update.component';
import { EnclosureEditViewComponent } from './components/enclosure-edit-view/enclosure-edit-view.component';
import { DeleteWarningComponent } from './components/delete-warning/delete-warning.component';
import { StopClickPropagationDirective } from './directives/stop-click-propagation.directive';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    EmployeeComponent,
    AnimalComponent,
    AnimalListComponent,
    EmployeeViewComponent,
    EnclosureComponent,
    EnclosureListComponent,
    EnclosureViewComponent,
    TaskCreationComponent,
    AnimalViewComponent,
    TaskListComponent,
    AssignTaskComponent,
    TaskListCommonComponent,
    AlertComponent,
    EnclosureEditViewComponent,
    TaskInfoUpdateComponent,
    DeleteWarningComponent,
    StopClickPropagationDirective,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
