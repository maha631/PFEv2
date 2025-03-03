import { Component, OnInit } from '@angular/core';
import { TestService } from '../../services/test.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-developer-test-list',
  templateUrl: './developer-test-list.component.html',
  styleUrls: ['./developer-test-list.component.css'],
  imports: [CommonModule], // Ajoute CommonModule

  standalone: true, // ❌ Supprimer cette ligne
})
export class DeveloperTestListComponent implements OnInit {
  tests: any[] = [];

  constructor(private testService: TestService) {}

  ngOnInit(): void {
    this.getAvailableTests();
  }

  getAvailableTests(): void {
    this.testService.getPublicTests().subscribe(
      (data) => {
        this.tests = data;
      },
      (error) => {
        console.error('Erreur lors du chargement des tests :', error);
      }
    );
  }
}
