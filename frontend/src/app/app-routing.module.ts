import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {EmployeeComponent} from './components/employee/employee.component';
import {AnimalComponent} from './components/animal/animal.component';
import {EmployeeViewComponent} from './components/employee-view/employee-view.component';
import {EnclosureComponent} from './components/enclosure/enclosure.component';
import {EnclosureViewComponent} from './components/enclosure-view/enclosure-view.component';
import {AnimalViewComponent} from './components/animal-view/animal-view.component';
import {EnclosureEditViewComponent} from './components/enclosure-edit-view/enclosure-edit-view.component';
import {EmployeeEditViewComponent} from './components/employee-edit-view/employee-edit-view.component';
import {EmployeePasswordChangeComponent} from './components/employee-password-change/employee-password-change.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'employee', canActivate: [AuthGuard], component: EmployeeComponent},
  {path: 'animal', canActivate: [AuthGuard], component: AnimalComponent},
  {path: 'employee-view/:username', canActivate: [AuthGuard], component: EmployeeViewComponent},
  {path: 'employee-edit-view/:username', canActivate: [AuthGuard], component: EmployeeEditViewComponent},
  {path: 'personal-info', canActivate: [AuthGuard], component: EmployeeViewComponent},
  {path: 'enclosure', canActivate: [AuthGuard], component: EnclosureComponent},
  {path: 'enclosure-view/:enclosureId', canActivate: [AuthGuard], component: EnclosureViewComponent},
  {path: 'enclosure-edit-view/:enclosureId', canActivate: [AuthGuard], component: EnclosureEditViewComponent},
  {path: 'animal-view/:animalId', canActivate: [AuthGuard], component: AnimalViewComponent},
  {path: 'employee-password-change/:username', canActivate: [AuthGuard], component: EmployeePasswordChangeComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
