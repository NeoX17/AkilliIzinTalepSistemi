import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Container,
  Typography,
  TextField,
  Paper,
  IconButton,
  InputAdornment,
  useTheme,
  useMediaQuery
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Login as LoginIcon
} from '@mui/icons-material';

const Login = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (email.includes('hr')) {
      localStorage.setItem('token', 'dummy-token-hr');
      localStorage.setItem('userRole', 'hr');
      navigate('/hr-panel');
    } else if (email.includes('employee')) {
      localStorage.setItem('token', 'dummy-token-employee');
      localStorage.setItem('userRole', 'employee');
      navigate('/employee-panel');
    } else {
      setError('Geçersiz e-posta veya şifre');
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(45deg, #1a237e 30%, #0d47a1 90%)'
      }}
    >
      <Container maxWidth="sm">
        <Paper
          elevation={24}
          sx={{
            p: 4,
            borderRadius: 2,
            background: 'rgba(255, 255, 255, 0.95)'
          }}
        >
          <Typography
            variant="h4"
            sx={{
              mb: 3,
              textAlign: 'center',
              fontWeight: 600,
              color: '#1a237e'
            }}
          >
            İzin Yönetim Sistemi
          </Typography>
          <Typography
            variant="body1"
            sx={{
              mb: 4,
              textAlign: 'center',
              color: 'text.secondary'
            }}
          >
            Talenteer İnşaat A.Ş.
          </Typography>
          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="E-posta"
              variant="outlined"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              sx={{ mb: 2 }}
            />
            <TextField
              fullWidth
              label="Şifre"
              type={showPassword ? 'text' : 'password'}
              variant="outlined"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowPassword(!showPassword)}
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 3 }}
            />
            {error && (
              <Typography
                color="error"
                sx={{ mb: 2, textAlign: 'center' }}
              >
                {error}
              </Typography>
            )}
            <Button
              fullWidth
              variant="contained"
              size="large"
              type="submit"
              startIcon={<LoginIcon />}
              sx={{
                background: 'linear-gradient(45deg, #1a237e 30%, #0d47a1 90%)',
                py: 1.5,
                fontSize: '1.1rem',
                '&:hover': {
                  background: 'linear-gradient(45deg, #0d47a1 30%, #1a237e 90%)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 6px 20px rgba(0, 0, 0, 0.2)'
                }
              }}
            >
              Giriş Yap
            </Button>
          </form>
        </Paper>
      </Container>
    </Box>
  );
};

export default Login; 