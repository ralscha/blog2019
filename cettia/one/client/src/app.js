import echarts from 'echarts/lib/echarts';
import 'echarts/lib/chart/pie';
import 'echarts/lib/chart/gauge';
import 'echarts/lib/chart/line';
import 'echarts/lib/chart/bar';
import cettia from 'cettia-client/cettia-bundler';

const pieChartNames = ['one', 'two', 'three', 'four', 'five'];

export default class App {
  constructor() {
    this.pieChart = echarts.init(document.getElementById('pie'));
    this.pieChart.setOption(this.pieChartOption());

    this.gaugeChart = echarts.init(document.getElementById('gauge'));
    this.gaugeChart.setOption(this.gaugeChartOption());

    this.lineData = [];
    this.lineChart = echarts.init(document.getElementById('line'));
    this.lineChart.setOption(this.lineChartOption());

    this.barChart = echarts.init(document.getElementById('bar'));
    this.barChart.setOption(this.barChartOption());

    this.startButton = document.getElementById('startButton');
    this.stopButton = document.getElementById('stopButton');

    startButton.addEventListener('click', this.start.bind(this));
    stopButton.addEventListener('click', this.stop.bind(this));

    this.chartCheckboxes = document.querySelectorAll('input[name=chart]');
    this.chartCheckboxes.forEach(item => item.addEventListener('change', this.handleChartCheckboxes.bind(this)));

    this.subscribedCharts = ['pie', 'gauge', 'line', 'bar'];

    this.eventHandlers = new Map();
    this.eventHandlers.set('pie', this.handlePieResponse.bind(this));
    this.eventHandlers.set('gauge', this.handleGaugeResponse.bind(this));
    this.eventHandlers.set('bar', this.handleBarResponse.bind(this));
    this.eventHandlers.set('line', this.handleLineResponse.bind(this));
  }

  start() {
    this.startButton.disabled = true;
    this.stopButton.disabled = false;
    this.socket = cettia.open('http://127.0.0.1:8080/cettia');

    this.socket.on("open", this.handleChartCheckboxes.bind(this));

    for (const [key, value] of this.eventHandlers.entries()) {
      this.socket.on(key, value);
    }
  }

  stop() {
    this.startButton.disabled = false;
    this.stopButton.disabled = true;
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  handleChartCheckboxes() {
    for (const [key, value] of this.eventHandlers.entries()) {
      this.socket.off(key, value);
    }

    const charts = [];
    this.chartCheckboxes.forEach(item => {
      if (item.checked) {
        charts.push(item.value);
        this.socket.on(item.value, this.eventHandlers.get(item.value));
        document.getElementById(item.value).style.display = 'inline-block';
      }
      else {
        document.getElementById(item.value).style.display = 'none';
      }
    });

    this.socket.send('charts', charts);
  }

  handlePieResponse(response) {
    const data = JSON.parse(response);
    const config = [];
    for (let i = 0; i < data.length; i++) {
      config.push({ value: data[i], name: pieChartNames[i] });
    }
    this.pieChart.setOption({
      series: {
        data: config
      }
    });
  }

  handleGaugeResponse(response) {
    this.gaugeChart.setOption({
      series: {
        data: [{ value: parseInt(response, 10), name: 'one' }]
      }
    });
  }

  handleLineResponse(response) {
    const data = JSON.parse(response);
    this.lineData.push(data);

    this.lineChart.setOption({
      series: {
        data: this.lineData
      }
    });
  }

  handleBarResponse(response) {
    const data = JSON.parse(response);
    this.barChart.setOption({
      series: {
        data
      }
    });
  }

  pieChartOption() {
    return {
      series: [
        {
          name: 'Random Data',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            normal: {
              show: true,
              position: 'outside'
            },
            emphasis: {
              show: true,
              textStyle: {
                fontSize: '30',
                fontWeight: 'bold'
              }
            }
          },
          labelLine: {
            normal: {
              show: false
            }
          },
          data: [
            { value: 335, name: 'one' },
            { value: 310, name: 'two' },
            { value: 234, name: 'three' },
            { value: 135, name: 'four' },
            { value: 1548, name: 'five' }
          ]
        }
      ]
    };
  }

  gaugeChartOption() {
    return {
      series: [{
        name: 'Random',
        type: 'gauge',
        axisLine: {
          lineStyle: {
            width: 10
          }
        },

        center: ['50%', '55%'],
        radius: '100%',

        data: [{ value: 50, name: 'one' }]
      }]
    };
  }

  barChartOption() {
    return {
      color: ['#3398DB'],
      min: 0,
      max: 300,
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          data: ['one', 'two', 'three', 'four', 'five', 'six', 'seven'],
          axisTick: {
            alignWithLabel: true
          }
        }
      ],
      yAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: 'Random Data',
          type: 'bar',
          barWidth: '60%',
          data: [10, 52, 200, 334, 390, 330, 220]
        }
      ]
    }
  }

  lineChartOption() {
    return {
      min: 0,
      max: 3000,
      xAxis: {
        type: 'time',
        splitLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        boundaryGap: [0, '100%'],
        splitLine: {
          show: false
        }
      },
      series: [{
        name: 'Random Data',
        type: 'line',
        showSymbol: false,
        hoverAnimation: false,
        data: []
      }]
    }
  }

}
