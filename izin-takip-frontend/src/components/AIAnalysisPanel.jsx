import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  CircularProgress,
  Paper,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  Button,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel
} from '@mui/material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import {
  TrendingUp,
  TrendingDown,
  Warning,
  CheckCircle,
  Error,
  Info,
  FilterList,
  Download
} from '@mui/icons-material';

const AIAnalysisPanel = ({ requests }) => {
  const [timeRange, setTimeRange] = useState('month');
  const [department, setDepartment] = useState('all');

  // Örnek veri - gerçek uygulamada API'den gelecek
  const analysisData = {
    approvalRate: 75,
    averageResponseTime: 2.5,
    trends: [
      { name: 'Ocak', onaylanan: 65, reddedilen: 15, bekleyen: 20 },
      { name: 'Şubat', onaylanan: 70, reddedilen: 10, bekleyen: 20 },
      { name: 'Mart', onaylanan: 75, reddedilen: 5, bekleyen: 20 },
      { name: 'Nisan', onaylanan: 80, reddedilen: 5, bekleyen: 15 },
    ],
    departmentStats: [
      { name: 'IT', value: 35 },
      { name: 'İK', value: 25 },
      { name: 'Satış', value: 20 },
      { name: 'Pazarlama', value: 20 },
    ],
    insights: [
      {
        type: 'positive',
        title: 'Onay Oranı Artışı',
        description: 'Son 3 ayda onay oranı %15 arttı',
        icon: <TrendingUp />
      },
      {
        type: 'warning',
        title: 'Yanıt Süresi',
        description: 'IT departmanında yanıt süreleri ortalamanın üzerinde',
        icon: <Warning />
      },
      {
        type: 'info',
        title: 'Talep Yoğunluğu',
        description: 'Pazartesi günleri talep yoğunluğu en yüksek seviyede',
        icon: <Info />
      }
    ]
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom sx={{ mb: 3 }}>
        AI Analiz Paneli
      </Typography>

      {/* Filtreler */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth>
              <InputLabel>Zaman Aralığı</InputLabel>
              <Select
                value={timeRange}
                label="Zaman Aralığı"
                onChange={(e) => setTimeRange(e.target.value)}
              >
                <MenuItem value="week">Son 1 Hafta</MenuItem>
                <MenuItem value="month">Son 1 Ay</MenuItem>
                <MenuItem value="quarter">Son 3 Ay</MenuItem>
                <MenuItem value="year">Son 1 Yıl</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth>
              <InputLabel>Departman</InputLabel>
              <Select
                value={department}
                label="Departman"
                onChange={(e) => setDepartment(e.target.value)}
              >
                <MenuItem value="all">Tüm Departmanlar</MenuItem>
                <MenuItem value="it">IT</MenuItem>
                <MenuItem value="hr">İK</MenuItem>
                <MenuItem value="sales">Satış</MenuItem>
                <MenuItem value="marketing">Pazarlama</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Button
              variant="contained"
              startIcon={<Download />}
              fullWidth
            >
              Rapor İndir
            </Button>
          </Grid>
        </Grid>
      </Paper>

      {/* Özet Kartları */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Onay Oranı
              </Typography>
              <Typography variant="h4" component="div">
                %{analysisData.approvalRate}
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                <TrendingUp color="success" />
                <Typography variant="body2" color="success.main" sx={{ ml: 1 }}>
                  +5% geçen aya göre
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Ortalama Yanıt Süresi
              </Typography>
              <Typography variant="h4" component="div">
                {analysisData.averageResponseTime} gün
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                <TrendingDown color="error" />
                <Typography variant="body2" color="error.main" sx={{ ml: 1 }}>
                  -0.5 gün geçen aya göre
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Toplam Talep
              </Typography>
              <Typography variant="h4" component="div">
                156
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                <TrendingUp color="success" />
                <Typography variant="body2" color="success.main" sx={{ ml: 1 }}>
                  +12% geçen aya göre
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Aktif Talepler
              </Typography>
              <Typography variant="h4" component="div">
                23
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                <Info color="info" />
                <Typography variant="body2" color="info.main" sx={{ ml: 1 }}>
                  Ortalama bekleme süresi: 1.2 gün
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Grafikler */}
      <Grid container spacing={2}>
        <Grid item xs={12} md={8}>
          <Card sx={{ width: '100%' }}>
            <CardContent sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
                Talep Trendleri
              </Typography>
              <Box sx={{ height: 250, width: '100%' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart 
                    data={analysisData.trends}
                    margin={{ top: 10, right: 20, left: 10, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis 
                      dataKey="name" 
                      padding={{ left: 10, right: 10 }}
                    />
                    <YAxis width={50} />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="onaylanan" fill="#2e7d32" name="Onaylanan" />
                    <Bar dataKey="reddedilen" fill="#d32f2f" name="Reddedilen" />
                    <Bar dataKey="bekleyen" fill="#ed6c02" name="Bekleyen" />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card sx={{ width: '100%' }}>
            <CardContent sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
                Departman Dağılımı
              </Typography>
              <Box sx={{ height: 250, width: '100%' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={analysisData.departmentStats}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    >
                      {analysisData.departmentStats.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend 
                      layout="vertical"
                      verticalAlign="middle"
                      align="right"
                    />
                  </PieChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* İçgörüler */}
      <Card sx={{ mt: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            AI İçgörüleri
          </Typography>
          <List>
            {analysisData.insights.map((insight, index) => (
              <React.Fragment key={index}>
                <ListItem>
                  <ListItemIcon>
                    {insight.icon}
                  </ListItemIcon>
                  <ListItemText
                    primary={insight.title}
                    secondary={insight.description}
                  />
                  <Chip
                    label={insight.type === 'positive' ? 'Pozitif' : 
                           insight.type === 'warning' ? 'Uyarı' : 'Bilgi'}
                    color={insight.type === 'positive' ? 'success' : 
                           insight.type === 'warning' ? 'warning' : 'info'}
                    size="small"
                  />
                </ListItem>
                {index < analysisData.insights.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        </CardContent>
      </Card>
    </Box>
  );
};

export default AIAnalysisPanel;
