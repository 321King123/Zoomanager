import {Component, Input, OnInit} from '@angular/core';
import {Enclosure} from '../../dtos/enclosure';

@Component({
  selector: 'app-enclosure-list',
  templateUrl: './enclosure-list.component.html',
  styleUrls: ['./enclosure-list.component.css']
})
export class EnclosureListComponent implements OnInit {
  @Input('enclosures') enclosures: Enclosure[];

  constructor() { }

  ngOnInit(): void {
  }

}
