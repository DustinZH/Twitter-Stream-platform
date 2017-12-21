
let option = {
    title: {
        text: 'Sentimental Analysis'
    },
    tooltip: {
        trigger: 'axis'
    },

    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    toolbox: {
        feature: {
            saveAsImage: {}
        }
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['12-02-2017','12-03-2017','12-04-2017']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name:'Positive',
            type:'line',
            stack: '总量',
            data:[120, 132, 101]
        },
        {
            name:'Negative',
            type:'line',
            stack: '总量',
            data:[220, 182, 191]
        },
        {
            name:'other',
            type:'line',
            stack: '总量',
            data:[150, 232, 201]
        }
    ]
};


export default option;