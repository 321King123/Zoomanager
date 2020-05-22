import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EnclosureService} from '../../services/enclosure.service';
import {Enclosure} from '../../dtos/enclosure';
@Component({
  selector: 'app-enclosure',
  templateUrl: './enclosure.component.html',
  styleUrls: ['./enclosure.component.css']
})
export class EnclosureComponent implements OnInit {
  error: boolean = false;
  errorMessage: string = '';

  enclosureCreationForm: FormGroup;
  submittedEnclosure = false;

  uploadedPicture: string;

  private fileType: string;

  enclosures: Enclosure[];

  constructor(private enclosureService: EnclosureService, private formBuilder: FormBuilder,
              private authService: AuthService) {
      this.initForm();
  }

  initForm(): void {
    this.enclosureCreationForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      description: [''],
      publicInformation: ['' ],
      picture: ['']
    });
  }

  ngOnInit(): void {
    this.getAllEnclosures();
  }

  getAllEnclosures() {
    this.enclosureService.getAllEnclosures().subscribe(
      enclosures => {
        this.enclosures = enclosures;
      },
      error => {
        if(error.status === 404) {
          this.enclosures.length = 0;
        }
        console.log('Failed to load all enclosures');
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  addEnclosure() {
    this.submittedEnclosure = true;
    if (this.enclosureCreationForm.valid) {
      const enclosure: Enclosure = new Enclosure(
        null,
        this.enclosureCreationForm.controls.name.value,
        this.enclosureCreationForm.controls.description.value,
        this.enclosureCreationForm.controls.publicInformation.value,
        this.uploadedPicture);
      this.createEnclosure(enclosure);
      this.clearForm();
    } else {
      console.log('Invalid input.');
    }
  }

  createEnclosure(enclosure: Enclosure) {
    this.enclosureService.createEnclosure(enclosure).subscribe(
      () => {
        this.getAllEnclosures();
      },
      error => {
          this.defaultServiceErrorHandling(error);
      });
  }

  public OnImageFileSelected(event)  {
    const files = event.target.files;
    const file = files[0];
    const maxSize = 259000000;
    const acceptedImageTypes = ['image/jpeg', 'image/png'];

    if (files && file) {
      if (file.size > maxSize) {
        this.error = true;
        this.errorMessage = 'File is to large. Max size is: ' + maxSize / 1000 + ' MB.';
      } else {
        if (!acceptedImageTypes.includes(file.type)) {
          this.error = true;
          this.errorMessage = 'File has to either be jpeg or png.';
        } else {
          const reader = new FileReader();

          reader.onload = this._handleReaderLoaded.bind(this);
          this.fileType = 'data:' + file.type.toString() + ';base64,';
          reader.readAsBinaryString(file);
        }
      }
    }
  }

  // From: https://stackoverflow.com/questions/42482951/converting-an-image-to-base64-in-angular-2
  // Converts the resulting binary String of the reader to base 64
  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    this.uploadedPicture = this.fileType + btoa(binaryString);
  }

  private clearForm() {
    this.enclosureCreationForm.reset();
    this.initForm();
    this.uploadedPicture = null;
    this.submittedEnclosure = false;
   }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }
}
