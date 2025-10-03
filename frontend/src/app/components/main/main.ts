import { Component } from '@angular/core';
import {SearchBanner} from '../search-banner/search-banner';
import {Results} from '../results/results';

@Component({
  selector: 'app-main',
  imports: [
    SearchBanner,
    Results
  ],
  templateUrl: './main.html',
  styleUrl: './main.scss'
})
export class Main {

}
