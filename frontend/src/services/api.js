import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Work Orders API
export const workOrdersApi = {
  getAll: () => api.get('/work-orders'),
  getById: (id) => api.get(`/work-orders/${id}`),
  create: (data) => api.post('/work-orders', data),
  update: (id, data) => api.put(`/work-orders/${id}`, data),
  delete: (id) => api.delete(`/work-orders/${id}`),
  start: (id) => api.post(`/work-orders/${id}/start`),
};

// Products API
export const productsApi = {
  getAll: () => api.get('/products'),
  getById: (id) => api.get(`/products/${id}`),
  create: (data) => api.post('/products', data),
  update: (id, data) => api.put(`/products/${id}`, data),
  delete: (id) => api.delete(`/products/${id}`),
};

// Processes API
export const processesApi = {
  getAll: () => api.get('/processes'),
  getById: (id) => api.get(`/processes/${id}`),
  create: (data) => api.post('/processes', data),
  update: (id, data) => api.put(`/processes/${id}`, data),
  delete: (id) => api.delete(`/processes/${id}`),
};

// Process Routing API
export const routingApi = {
  getByProduct: (productId) => api.get(`/routing/product/${productId}`),
  create: (data) => api.post('/routing', data),
  update: (id, data) => api.put(`/routing/${id}`, data),
  delete: (id) => api.delete(`/routing/${id}`),
};

// Equipment API
export const equipmentApi = {
  getAll: () => api.get('/equipment'),
  getById: (id) => api.get(`/equipment/${id}`),
  getStatus: () => api.get('/equipment/status'),
};

// Defects API
export const defectsApi = {
  getAll: () => api.get('/defects'),
  getById: (id) => api.get(`/defects/${id}`),
  create: (data) => api.post('/defects', data),
  getStats: () => api.get('/defects/stats'),
};

// Work Results API
export const workResultsApi = {
  getAll: () => api.get('/work-results'),
  getByWorkOrder: (workOrderId) => api.get(`/work-results/work-order/${workOrderId}`),
  create: (data) => api.post('/work-results', data),
};

// Dashboard API
export const dashboardApi = {
  getStats: () => api.get('/dashboard/stats'),
  getProductionChart: () => api.get('/dashboard/production-chart'),
  getDefectRate: () => api.get('/dashboard/defect-rate'),
};

export default api;