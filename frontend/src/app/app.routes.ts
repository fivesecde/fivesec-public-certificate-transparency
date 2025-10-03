import {Routes} from '@angular/router';
import {Main} from './components/main/main';
import {About} from './components/about/about';
import {Faq} from './components/faq/faq';

export const routes: Routes = [
  {
    path: 'start', component: Main, pathMatch: 'full',
  },
  {
    path: 'about', component: About, pathMatch: 'full',
  },
  {
    path: 'faq', component: Faq, pathMatch: 'full',
  },
  {path: '**', redirectTo: 'start'}
];

