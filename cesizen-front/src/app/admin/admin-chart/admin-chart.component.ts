import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration } from 'chart.js/auto';

@Component({
  selector: 'app-admin-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-chart.component.html',
  styleUrls: ['./admin-chart.component.scss']
})
export class AdminChartComponent implements OnInit {

  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
  chart!: Chart;

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin'],
        datasets: [
          {
            label: 'Diagnostics réalisés',
            data: [5, 12, 9, 14, 20, 18],
            borderColor: '#3f51b5',
            backgroundColor: 'rgba(63, 81, 181, 0.3)',
            tension: 0.3
          }
        ]
      }
    };

    this.chart = new Chart(this.chartCanvas.nativeElement, config);
  }
}
