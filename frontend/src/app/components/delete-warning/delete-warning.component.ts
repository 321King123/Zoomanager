import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

import $ from 'jquery';

@Component({
  selector: 'app-delete-warning',
  templateUrl: './delete-warning.component.html',
  styleUrls: ['./delete-warning.component.css']
})
export class DeleteWarningComponent implements OnInit {
  @Input() stringId: String = 'default';
  @Input() deletionSubjectType: String = 'Type';
  @Input() readableSubjectIdentifier: String = 'NoSubjectGiven';
  @Output() deleteSubject = new EventEmitter();
  @Input() delBtnText: String = 'Delete';

  @Output() toggleClickPropagationEvent = new EventEmitter();

  @Input() outlinedButtons = true;
  @Input() smallButton = false;

  showNormalButton = true;
  @Input() showEditOrDeleteButton = false;

  @Output() editSubject = new EventEmitter();

  eOdBtnIsDelete = false;
  eOdBtnIsEdit = true;

  @ViewChild('modalToggleBtn')
  modalToggle: ElementRef<HTMLElement>;

  private modalIsOpen: boolean = false;

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

  // toggleModal() {
  //   DEBUG_LOG('Toggle Modal');
  //   const modalToggleElement: HTMLElement = this.modalToggle.nativeElement;
  //   modalToggleElement.click();
  // }

  toggleModal() {
 //   this.stopClickPropagationEvent.emit();
    console.log('Toggle Modal ' + this.readableSubjectIdentifier);
    const modalToggleElement: HTMLElement = this.modalToggle.nativeElement;
    if (this.modalIsOpen) {
      this.closeModal(modalToggleElement);
    } else if (!this.modalIsOpen) {
      this.openModal(modalToggleElement);
    }

    // modalToggleElement.click();
    // this.enableClickPropagationEvent.emit();
  }

  closeModal(modalToggleElement: HTMLElement) {
    console.log('close modal, modalIsOpen = ' + this.modalIsOpen);
    if (this.modalIsOpen) {
      modalToggleElement.click();
      this.modalIsOpen = false;
      this.toggleClickPropagationEvent.emit();
    }

  }

  openModal(modalToggleElement: HTMLElement) {
    console.log('open modal, modalIsOpen = ' + this.modalIsOpen);
    if (!this.modalIsOpen) {
      this.toggleClickPropagationEvent.emit();
      this.modalIsOpen = true;
      modalToggleElement.click();
    }
  }

}
