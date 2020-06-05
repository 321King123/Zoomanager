import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-delete-warning',
  templateUrl: './delete-warning.component.html',
  styleUrls: ['./delete-warning.component.css']
})
export class DeleteWarningComponent implements OnInit {
  @Input() deletionSubjectType: String = 'Type';
  @Input() readableSubjectIdentifier: String = 'NoSubjectGiven';
  @Output() deleteSubject = new EventEmitter();
  @Input() delBtnText: String = 'Delete';

  @Input() outlinedButtons = true;
  @Input() smallButton = false;

  showNormalButton = true;
  @Input() showEditOrDeleteButton = false;

  @Output() editSubject = new EventEmitter();

  eOdBtnIsDelete = false;
  eOdBtnIsEdit = true;

  // TODO: make to buttons with callbacks one for just deletion, one for deletion or editing (dropdown)
  // and two bools for setting true depending on what to use, if both are false use default ( just deletion)

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
    if (this.showEditOrDeleteButton) {
      this.showNormalButton = false;
    }
  }

  toDeleteButton() {
    this.eOdBtnIsDelete = true;
    this.eOdBtnIsEdit = false;
  }

  toEditButton() {
    this.eOdBtnIsEdit = true;
    this.eOdBtnIsDelete = false;
  }

}
