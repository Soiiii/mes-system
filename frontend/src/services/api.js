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

// LOT API
export const lotsApi = {
  getAll: () => api.get('/lots'),
  getById: (id) => api.get(`/lots/${id}`),
  getByLotNumber: (lotNumber) => api.get(`/lots/number/${lotNumber}`),
  create: (data) => api.post('/lots', data),
  updateStatus: (id, status) => api.put(`/lots/${id}/status`, null, { params: { status } }),
  addHistory: (data) => api.post('/lots/history', data),
  getHistory: (id) => api.get(`/lots/${id}/history`),
  getHistoryByLotNumber: (lotNumber) => api.get(`/lots/number/${lotNumber}/history`),
  getByProduct: (productId) => api.get(`/lots/product/${productId}`),
  getByWorkOrder: (workOrderId) => api.get(`/lots/work-order/${workOrderId}`),
  search: (keyword) => api.get('/lots/search', { params: { keyword } }),
};

// Quality Inspection API
export const qualityInspectionApi = {
  getAll: () => api.get('/quality-inspections'),
  getById: (id) => api.get(`/quality-inspections/${id}`),
  create: (data) => api.post('/quality-inspections', data),
  complete: (id, result) => api.put(`/quality-inspections/${id}/complete`, null, { params: { result } }),
  getByLot: (lotId) => api.get(`/quality-inspections/lot/${lotId}`),
  getByType: (type) => api.get(`/quality-inspections/type/${type}`),
  getByResult: (result) => api.get(`/quality-inspections/result/${result}`),
  
  // Standards
  getAllStandards: () => api.get('/quality-inspections/standards'),
  createStandard: (data) => api.post('/quality-inspections/standards', data),
  getStandardsByProduct: (productId, type) => 
    api.get(`/quality-inspections/standards/product/${productId}`, { params: { type } }),
};

export default api;