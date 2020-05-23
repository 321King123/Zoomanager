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

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'employee', canActivate: [AuthGuard], component: EmployeeComponent},
  {path: 'animal', canActivate: [AuthGuard], component: AnimalComponent},
  {path: 'employee-view/:username', canActivate: [AuthGuard], component: EmployeeViewComponent},
  {path: 'personal-info', canActivate: [AuthGuard], component: EmployeeViewComponent},
  {path: 'enclosure', canActivate: [AuthGuard], component: EnclosureComponent},
  {path: 'enclosure-view/:enclosureId', canActivate: [AuthGuard], component: EnclosureViewComponent},
  {path: 'animal-view/:animalId', canActivate: [AuthGuard], component: AnimalViewComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
