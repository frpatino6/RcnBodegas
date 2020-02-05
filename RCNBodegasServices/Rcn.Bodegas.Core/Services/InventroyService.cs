using Microsoft.Extensions.Logging;
using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
    public class InventroyService : IInventroyService
    {

        private const string WAREHOUSE_TYPE_V = "V";
        private readonly IOracleManagment _IOracleManagment;
        private readonly ILogger<InventroyService> _logger;
        public InventroyService(IOracleManagment oracleManagment, ILogger<InventroyService> logger)
        {
            _IOracleManagment = oracleManagment;
            _logger = logger;
        }

        public Task<bool> CreateInconsistency(string warehouseType, string productionId, int responsibleId)
        {
            OracleParameter OraEmpresa = new OracleParameter(":AD_EMPRESA_CODIGO", OracleDbType.Int32, 20, ParameterDirection.Input);
            OracleParameter OraCodigo = new OracleParameter(":new_id", OracleDbType.Int32, ParameterDirection.Output);
            OracleParameter OraTipoBodega = new OracleParameter(":CODIGO_TIPO_BODEGA", OracleDbType.Varchar2, 1, ParameterDirection.Input);
            OracleParameter OraProduccion = new OracleParameter(":CODIGO_PRODUCCION", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraResponsable = new OracleParameter(":CODIGO_RESPONSABLE", OracleDbType.Int32, 1, ParameterDirection.Input);
            OracleParameter OraTipoElemento = new OracleParameter(":CODIGO_TIPO_ELEMENTO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraCodigoBarras = new OracleParameter(":CODIGO_BARRAS", OracleDbType.Varchar2, 20, ParameterDirection.Input);
            OracleParameter OraFechaInventario = new OracleParameter(":FECHA_INVENTARIO", OracleDbType.Date, ParameterDirection.Input);
            OracleParameter OraHoraInventario = new OracleParameter(":HORA_INVENTARIO", OracleDbType.TimeStamp, 1, ParameterDirection.Input);
            OracleParameter OraEncontrado = new OracleParameter(":ENCONTRADO", OracleDbType.Int16, ParameterDirection.Input);



            string query = $@" INSERT INTO BD_IMAGENES (AD_EMPRESA_CODIGO,CODIGO_TIPO_BODEGA,CODIGO_PRODUCCION,CODIGO_RESPONSABLE,CODIGO_TIPO_ELEMENTO,CODIGO_BARRAS,FECHA_INVENTARIO,HORA_INVENTARIO,ENCONTRADO)
                                          VALUES(:AD_EMPRESA_CODIGO,:CODIGO_TIPO_BODEGA,:CODIGO_PRODUCCION,:CODIGO_RESPONSABLE,:CODIGO_TIPO_ELEMENTO,:CODIGO_BARRAS,:FECHA_INVENTARIO,:HORA_INVENTARIO,:ENCONTRADO) returning CODIGO into :new_id";

            using (OracleConnection con = new OracleConnection(_IOracleManagment.GetOracleConnectionParameters()))
            {
                con.Open();
                using (OracleCommand oraUpdate = con.CreateCommand())
                {
                    using (OracleTransaction transaction = con.BeginTransaction(IsolationLevel.ReadCommitted))
                    {
                        oraUpdate.Transaction = transaction;
                        try
                        {
                            oraUpdate.CommandText = query;
                            oraUpdate.Parameters.Add(OraEmpresa);
                            oraUpdate.Parameters.Add(OraCodigo);
                            oraUpdate.Parameters.Add(OraTipoBodega);
                            oraUpdate.Parameters.Add(OraProduccion);
                            oraUpdate.Parameters.Add(OraResponsable);
                            oraUpdate.Parameters.Add(OraTipoElemento);
                            oraUpdate.Parameters.Add(OraCodigoBarras);
                            oraUpdate.Parameters.Add(OraFechaInventario);
                            oraUpdate.Parameters.Add(OraHoraInventario);
                            oraUpdate.Parameters.Add(OraEncontrado);

                            OraEmpresa.Value = 1;
                            OraTipoBodega.Value = 1;
                            OraProduccion.Value = 1;
                            OraResponsable.Value = 1;
                            OraTipoElemento.Value = 1;
                            OraCodigoBarras.Value = 1;
                            OraFechaInventario.Value = 1;
                            OraHoraInventario.Value = 1;
                            OraEncontrado.Value = 1;


                        }
                        catch (System.Exception)
                        {
                            transaction.Rollback();
                            throw;
                        }
                    }
                }

                return null;
            }
        }

        /// <summary>
        /// Create BD_INVENTARIO record
        /// </summary>
        /// <param name="invetoryViewModel"></param>
        /// <returns></returns>
        public int CreateInventoryHeader(InvetoryHeaderViewModel invetoryViewModel)
        {
            int rowCount = 0;

            OracleParameter OraEmpresa = new OracleParameter(":AD_EMPRESA_CODIGO", OracleDbType.Int32, 20, ParameterDirection.Input);
            OracleParameter OraProduccion = new OracleParameter(":CODIGO_PRODUCCION", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraResponsable = new OracleParameter(":CODIGO_RESPONSABLE", OracleDbType.Int32, 1, ParameterDirection.Input);
            OracleParameter OraTipoBodega = new OracleParameter(":CODIGO_TIPO_BODEGA", OracleDbType.Varchar2, 1, ParameterDirection.Input);
            OracleParameter OraEstado = new OracleParameter(":ESTADO", OracleDbType.Varchar2, 1, ParameterDirection.Input);
            OracleParameter OraInventoryUser = new OracleParameter(":USUARIO_LOGUIN", OracleDbType.Varchar2, 20, ParameterDirection.Input);
            OracleParameter OraCodigoElemento = new OracleParameter("new_id", OracleDbType.Int32)
            {
                Direction = ParameterDirection.Output
            };

            string query = $@" INSERT INTO BD_INVENTARIO (CODIGO,AD_EMPRESA_CODIGO,CODIGO_PRODUCCION,CODIGO_RESPONSABLE,CODIGO_TIPO_BODEGA,ESTADO,USUARIO_LOGUIN,FECHA_FINAL,FECHA_INICIAL)
                                          VALUES(BD_INVENTARIO_SEQ.nextval,:AD_EMPRESA_CODIGO,:CODIGO_PRODUCCION,:CODIGO_RESPONSABLE,:CODIGO_TIPO_BODEGA, :ESTADO, :USUARIO_LOGUIN,TO_DATE('" + invetoryViewModel.EndDate + "', 'YYYY-MM-DD HH24:mi:ss'), TO_DATE('" + invetoryViewModel.InitDate + "', 'YYYY-MM-DD HH24:mi:ss')) returning CODIGO into :new_id ";

            using (OracleConnection con = new OracleConnection(_IOracleManagment.GetOracleConnectionParameters()))
            {
                con.Open();
                using (OracleCommand oraUpdate = con.CreateCommand())
                {
                    using (OracleTransaction transaction = con.BeginTransaction(IsolationLevel.ReadCommitted))
                    {
                        oraUpdate.Transaction = transaction;
                        try
                        {
                            oraUpdate.CommandText = query;
                            oraUpdate.Parameters.Add(OraEmpresa);
                            oraUpdate.Parameters.Add(OraProduccion);
                            oraUpdate.Parameters.Add(OraResponsable);
                            oraUpdate.Parameters.Add(OraTipoBodega);
                            oraUpdate.Parameters.Add(OraEstado);
                            oraUpdate.Parameters.Add(OraInventoryUser);
                            oraUpdate.Parameters.Add(OraCodigoElemento);


                            OraEmpresa.Value = invetoryViewModel.Company;
                            OraTipoBodega.Value = invetoryViewModel.WarehouseTypeId;
                            OraProduccion.Value = invetoryViewModel.ProductionId;
                            OraResponsable.Value = invetoryViewModel.ResponsibleId;
                            OraEstado.Value = 0;
                            OraInventoryUser.Value = invetoryViewModel.InventoryUser;

                            rowCount = oraUpdate.ExecuteNonQuery();

                            CreateInventoryDetail(invetoryViewModel, Convert.ToInt32(OraCodigoElemento.Value.ToString()), invetoryViewModel.TypeElement);

                            transaction.Commit();
                        }
                        catch (System.Exception)
                        {
                            transaction.Rollback();
                            throw;
                        }
                    }
                }

                return Convert.ToInt32(OraCodigoElemento.Value.ToString());
            }
        }

        /// <summary>
        /// Create BD_DETALLE_INVENTARIO record
        /// </summary>
        /// <param name="invetoryViewModel"></param>
        /// <returns></returns>
        public int CreateInventoryDetail(InventoryDetailViewModel inventoryDetailViewModel, int status)
        {
            int rowCount = 0;

            OracleParameter OraMaterilCodigo = new OracleParameter(":BD_MTRIAL_CODIGO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraCodigo = new OracleParameter(":CODIGO_INVENTARIO", OracleDbType.Int32, 20, ParameterDirection.Input);
            OracleParameter OraUser = new OracleParameter(":USUARIO_LOGUIN", OracleDbType.Varchar2, 20, ParameterDirection.Input);


            string query = $@" UPDATE BD_DETALLE_INVENTARIO SET ENCONTRADO = 1, FECHA_ENTREGA=TO_DATE('" + inventoryDetailViewModel.DeliveryDate + "', 'YYYY-MM-DD HH24:mi:ss'), USUARIO_LOGUIN=:USUARIO_LOGUIN,FECHA_ENCONTRADO=TO_DATE('" + inventoryDetailViewModel.FoundDate + "', 'YYYY-MM-DD HH24:mi:ss') " +
                "WHERE BD_MTRIAL_CODIGO=:BD_MTRIAL_CODIGO AND CODIGO_INVENTARIO=:CODIGO_INVENTARIO";

            using (OracleConnection con = new OracleConnection(_IOracleManagment.GetOracleConnectionParameters()))
            {
                con.Open();
                using (OracleCommand oraUpdate = con.CreateCommand())
                {
                    using (OracleTransaction transaction = con.BeginTransaction(IsolationLevel.ReadCommitted))
                    {
                        oraUpdate.Transaction = transaction;
                        try
                        {
                            oraUpdate.CommandText = query;
                            oraUpdate.Parameters.Add(OraUser);
                            oraUpdate.Parameters.Add(OraMaterilCodigo);
                            oraUpdate.Parameters.Add(OraCodigo);


                            OraMaterilCodigo.Value = inventoryDetailViewModel.ElementId;
                            OraCodigo.Value = inventoryDetailViewModel.InventoryId;
                            OraUser.Value = inventoryDetailViewModel.InventoryUser;
                            rowCount = oraUpdate.ExecuteNonQuery();
                            transaction.Commit();
                        }
                        catch (System.Exception)
                        {
                            transaction.Rollback();
                            throw;
                        }
                    }
                }
            }
            UpdateStateInventory(inventoryDetailViewModel.InventoryId, status);
            return inventoryDetailViewModel.InventoryId;
        }

        private int CreateInventoryDetail(InvetoryHeaderViewModel invetoryHeaderViewModel, int inventoryId, int typeElementId)
        {

            OracleParameter OpCodProduction = new OracleParameter
            {
                DbType = DbType.Int32,
                Value = invetoryHeaderViewModel.ProductionId,
                ParameterName = "COD_PRODUCCION"
            };

            OracleParameter OpwarehouseType = new OracleParameter
            {
                DbType = DbType.String,
                Value = invetoryHeaderViewModel.WarehouseTypeId,
                ParameterName = "COD_TIPO_BODEGA"
            };

            OracleParameter OpCodResponsible = new OracleParameter
            {
                DbType = DbType.Int64,
                Value = invetoryHeaderViewModel.ResponsibleId,
                ParameterName = "COD_RESPONSIBLE"
            };

            OracleParameter OpCodTypeElement = new OracleParameter
            {
                DbType = DbType.Int32,
                Value = 1,
                ParameterName = "TIPO_ELEMENTO_HEADER"
            };

            string queryDetail = GetQueryMaterialsForProduction(
                invetoryHeaderViewModel.WarehouseTypeId,
                invetoryHeaderViewModel.ProductionId,
                invetoryHeaderViewModel.ResponsibleId, invetoryHeaderViewModel.TypeElement, 0, inventoryId, invetoryHeaderViewModel.InventoryUser, invetoryHeaderViewModel.FechaMovimiento);

            string query = $@" INSERT INTO BD_DETALLE_INVENTARIO (BD_MTRIAL_CODIGO,CODIGO_INVENTARIO,CODIGO_RESPONSABLE,DESCRIPCION_ESTADO,ENCONTRADO,TIPO_ELEMENTO,FECHA_ENTREGA,USUARIO_LOGUIN)
                                        {queryDetail} ";

            using (OracleConnection con = new OracleConnection(_IOracleManagment.GetOracleConnectionParameters()))
            {
                con.Open();
                using (OracleCommand oraUpdate = con.CreateCommand())
                {
                    using (OracleTransaction transaction = con.BeginTransaction(IsolationLevel.ReadCommitted))
                    {
                        oraUpdate.Transaction = transaction;
                        try
                        {
                            oraUpdate.CommandText = query;
                            oraUpdate.Parameters.Add(OpwarehouseType);
                            oraUpdate.Parameters.Add(OpCodProduction);

                            if (invetoryHeaderViewModel.ResponsibleId != -1)
                            {
                                oraUpdate.Parameters.Add(OpCodResponsible);
                            }

                            if (typeElementId != -1)
                            {
                                oraUpdate.Parameters.Add(OpCodTypeElement);
                            }

                            int rowCount = oraUpdate.ExecuteNonQuery();

                            transaction.Commit();
                        }
                        catch (System.Exception)
                        {
                            transaction.Rollback();
                            throw;
                        }
                    }
                }

                return 0;
            }
        }

        /// <summary>
        /// Get list productions
        /// </summary>
        /// <param name="wareHouse"></param>
        /// <returns></returns>
        public async Task<List<ProductionViewModel>> GetListProductions(string wareHouse)
        {
            _logger.LogInformation("GetListProductions");
            List<ProductionViewModel> result = new List<ProductionViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = @"select * from V_PRODUCCION  ORDER BY PRODUCCION";

            OracleParameter opwareHouse = new OracleParameter
            {
                DbType = DbType.String,
                Value = wareHouse,
                ParameterName = "wharehouse"
            };
            parameters.Add(opwareHouse);


            DataSet records = _IOracleManagment.GetDataSet(parameters, query);


            foreach (DataTable table in records.Tables)
            {

                foreach (DataRow dr in table.Rows)
                {
                    string id = dr["CODIGO_TIPO_BODEGA"].ToString();
                    string name = dr["NOMBRE_TIPO_BODEGA"].ToString();
                    int cod = Convert.ToInt32(dr["CODIGO_PRODUCCION"].ToString());
                    string prod = dr["PRODUCCION"].ToString();
                    result.Add(new ProductionViewModel
                    {
                        Id = id,
                        InternalOrder = string.Empty,
                        NameWareHouseType = name,
                        ProductionCode = cod,
                        ProductionName = prod
                    });

                }
            }
            _logger.LogInformation($"result for GetListProductions {result.Count}");
            return result;
        }

        /// <summary>
        /// Get list responsible
        /// </summary>
        /// <param name="wareHouse"></param>
        /// <param name="production"></param>
        /// <returns></returns>
        public async Task<List<ResponsibleViewModel>> GetListResponsible(string wareHouse, string production)
        {
#if DEBUG
            _logger.LogInformation("Debug mode... ");
#else
        _logger.LogInformation("release mode... ");
#endif
            _logger.LogInformation("Ejecutando servicio GetListResponsable ");
            List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();

            string query = @"SELECT M.pa_tercero_codigo  Codigo_Responsable,
                       (  Select Nombre 
                          From GN_TERCERO
                          Where codigo = M.pa_tercero_codigo) Nombre_Responsable,M.BD_Ubccion_Codigo Codigo_Produccion
                          ,
                       (
                          Select NOMBRE 
                          From BD_UBICACION 
                          Where Codigo = M.bd_ubccion_codigo) Produccion 
                    FROM bd_movimiento_material M
                    WHERE M.BD_Ubccion_Codigo  =:production
                    GROUP BY M.pa_tercero_codigo, M.BD_Ubccion_Codigo";

            _logger.LogInformation("Query " + query);

            OracleParameter opwareHouse = new OracleParameter
            {
                DbType = DbType.String,
                Value = wareHouse,
                ParameterName = "warehouse"
            };
            parameters.Add(opwareHouse);

            OracleParameter opProduccion = new OracleParameter
            {
                DbType = DbType.String,
                Value = production,
                ParameterName = "production"
            };

            parameters.Add(opProduccion);

            DataSet records = _IOracleManagment.GetDataSet(parameters, query);

            foreach (DataTable table in records.Tables)
            {
                _logger.LogInformation("Iterando registros");
                foreach (DataRow dr in table.Rows)
                {
                    string id = dr["CODIGO_RESPONSABLE"].ToString();
                    string name = dr["NOMBRE_RESPONSABLE"].ToString();

                    result.Add(new ResponsibleViewModel
                    {
                        Id = id,
                        Name = name
                    });

                }
            }

            _logger.LogInformation("Registros retornados " + result.Count.ToString());
            return result;
        }

        /// <summary>
        /// Get list TIPO_ELEMENTO
        /// </summary>
        /// <returns></returns>
        public async Task<List<TipoElementoViewModel>> GetListTipoElemento(string wareHouseid)
        {
            List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = string.Empty;

            if (wareHouseid.Equals(WAREHOUSE_TYPE_V))
            {
                query = @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_PRENDA  WHERE ESTADO='A' ORDER BY NOMBRE_TIPO";
            }
            else
            {
                query = @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_ELEMENTO WHERE ESTADO='A' ORDER BY NOMBRE_TIPO ";
            }

            IEnumerable<IDataRecord> records = _IOracleManagment.GetData(null, query);

            foreach (IDataRecord rec in records)
            {

                int id = rec.GetInt32(rec.GetOrdinal("CODIGO_TIPO"));
                string name = rec.GetString(rec.GetOrdinal("NOMBRE_TIPO"));

                result.Add(new TipoElementoViewModel
                {
                    Id = id,
                    Name = name
                });
            }

            return result;
        }

        public async Task<List<ResponsibleViewModel>> GetListWarehouseUserAsync(string tipoBodega)
        {
            List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = @"select unique codigo_responsable codigo_responsable,nombre_responsable
                  from V_RESPONSABLE
                  where codigo_produccion in (1,2)  and DECODE(codigo_produccion,1,'V','A') = :TIPO_BODEGA
                  ORDER BY NOMBRE_RESPONSABLE";

            OracleParameter opTipoBodega = new OracleParameter
            {
                DbType = DbType.String,
                Value = tipoBodega,
                ParameterName = "TIPO_BODEGA"
            };
            parameters.Add(opTipoBodega);


            DataSet records = _IOracleManagment.GetDataSet(parameters, query);

            foreach (DataTable table in records.Tables)
            {
                _logger.LogInformation("Iterando registros");
                foreach (DataRow dr in table.Rows)
                {
                    string id = dr["CODIGO_RESPONSABLE"].ToString();
                    string name = dr["NOMBRE_RESPONSABLE"].ToString();

                    result.Add(new ResponsibleViewModel
                    {
                        Id = id,
                        Name = name
                    });

                }
            }

            return result;
        }

        /// <summary>
        /// Retorna el ultimo inventario pendiente por cerrar
        /// </summary>
        /// <param name="userName"></param>
        /// <returns></returns>
        public async Task<List<InvetoryHeaderViewModel>> GetPendingInventoryByUser(string userName)
        {
            _logger.LogInformation("GetPendingInventoryByUser");

            List<InvetoryHeaderViewModel> result = null;
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = @"SELECT I.* , u.NOMBRE AS NOMBRE_PRODUCCION FROM BD_INVENTARIO I
                            INNER JOIN BD_UBICACION U ON I.CODIGO_PRODUCCION =U.CODIGO
                            WHERE USUARIO_LOGUIN=:USUARIO_LOGUIN AND I.ESTADO=0 ORDER BY I.CODIGO DESC";

            OracleParameter opwareHouse = new OracleParameter
            {
                DbType = DbType.String,
                Value = userName,
                ParameterName = "USUARIO_LOGUIN"
            };
            parameters.Add(opwareHouse);

            DataSet records = _IOracleManagment.GetDataSet(parameters, query);
            foreach (DataTable table in records.Tables)
            {
                foreach (DataRow dr in table.Rows)
                {
                    int empresa = Convert.ToInt32(dr["AD_EMPRESA_CODIGO"].ToString());
                    string nombre_produccion = dr["NOMBRE_PRODUCCION"].ToString();
                    int codigo = Convert.ToInt32(dr["CODIGO"].ToString());
                    string tipo_bodega = dr["CODIGO_TIPO_BODEGA"].ToString();
                    int produccion = Convert.ToInt32(dr["CODIGO_PRODUCCION"].ToString());
                    int responsable = Convert.ToInt32(dr["CODIGO_RESPONSABLE"].ToString());
                    string fecha_inventario = dr["FECHA_INICIAL"].ToString();
                    int estado = Convert.ToInt32(dr["ESTADO"].ToString());
                    InvetoryHeaderViewModel invetoryHeaderViewModel = new InvetoryHeaderViewModel
                    {
                        Company = empresa,
                        Id = codigo,
                        InitDate = fecha_inventario,
                        ProductionId = produccion,
                        ResponsibleId = responsable,
                        WarehouseTypeId = tipo_bodega,
                        State = estado,
                        productionName = nombre_produccion
                    };

                    if (result == null)
                    {
                        result = new List<InvetoryHeaderViewModel>();
                    }
                    result.Add(invetoryHeaderViewModel);
                }
            }
            _logger.LogInformation($"result for GetListProductions {result}");
            return result;
        }

        public async Task<List<InvetoryHeaderViewModel>> GetPendingInventoryById(int id)
        {
            _logger.LogInformation("GetPendingInventoryByUser");

            List<InvetoryHeaderViewModel> result = null;
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = @"SELECT I.* , u.NOMBRE AS NOMBRE_PRODUCCION FROM BD_INVENTARIO I
                            INNER JOIN BD_UBICACION U ON I.CODIGO_PRODUCCION =U.CODIGO
                            WHERE I.CODIGO=:CODIGO AND I.ESTADO=0 ORDER BY I.CODIGO DESC";

            OracleParameter opwareHouse = new OracleParameter
            {
                DbType = DbType.Int32,
                Value = id,
                ParameterName = "CODIGO"
            };
            parameters.Add(opwareHouse);

            DataSet records = _IOracleManagment.GetDataSet(parameters, query);
            foreach (DataTable table in records.Tables)
            {
                foreach (DataRow dr in table.Rows)
                {
                    int empresa = Convert.ToInt32(dr["AD_EMPRESA_CODIGO"].ToString());
                    string nombre_produccion = dr["NOMBRE_PRODUCCION"].ToString();
                    int codigo = Convert.ToInt32(dr["CODIGO"].ToString());
                    string tipo_bodega = dr["CODIGO_TIPO_BODEGA"].ToString();
                    int produccion = Convert.ToInt32(dr["CODIGO_PRODUCCION"].ToString());
                    int responsable = Convert.ToInt32(dr["CODIGO_RESPONSABLE"].ToString());
                    string fecha_inventario = dr["FECHA_INICIAL"].ToString();
                    int estado = Convert.ToInt32(dr["ESTADO"].ToString());
                    InvetoryHeaderViewModel invetoryHeaderViewModel = new InvetoryHeaderViewModel
                    {
                        Company = empresa,
                        Id = codigo,
                        InitDate = fecha_inventario,
                        ProductionId = produccion,
                        ResponsibleId = responsable,
                        WarehouseTypeId = tipo_bodega,
                        State = estado,
                        productionName = nombre_produccion
                    };

                    if (result == null)
                    {
                        result = new List<InvetoryHeaderViewModel>();
                    }
                    result.Add(invetoryHeaderViewModel);
                }
            }
            _logger.LogInformation($"result for GetListProductions {result}");
            return result;
        }

        public async Task<List<InvetoryHeaderViewModel>> GetPendingInventory()
        {
            _logger.LogInformation("GetPendingInventoryByUser");

            List<InvetoryHeaderViewModel> result = null;

            string query = @"SELECT I.* , u.NOMBRE AS NOMBRE_PRODUCCION FROM BD_INVENTARIO I
                            INNER JOIN BD_UBICACION U ON I.CODIGO_PRODUCCION =U.CODIGO
                            WHERE  I.ESTADO=0 ORDER BY I.CODIGO DESC";

            DataSet records = _IOracleManagment.GetDataSet(null, query);
            foreach (DataTable table in records.Tables)
            {
                foreach (DataRow dr in table.Rows)
                {
                    int empresa = Convert.ToInt32(dr["AD_EMPRESA_CODIGO"].ToString());
                    string nombre_produccion = dr["NOMBRE_PRODUCCION"].ToString();
                    int codigo = Convert.ToInt32(dr["CODIGO"].ToString());
                    string tipo_bodega = dr["CODIGO_TIPO_BODEGA"].ToString();
                    int produccion = Convert.ToInt32(dr["CODIGO_PRODUCCION"].ToString());
                    int responsable = Convert.ToInt32(dr["CODIGO_RESPONSABLE"].ToString());
                    string fecha_inventario = dr["FECHA_INICIAL"].ToString();
                    int estado = Convert.ToInt32(dr["ESTADO"].ToString());
                    InvetoryHeaderViewModel invetoryHeaderViewModel = new InvetoryHeaderViewModel
                    {
                        Company = empresa,
                        Id = codigo,
                        InitDate = fecha_inventario,
                        ProductionId = produccion,
                        ResponsibleId = responsable,
                        WarehouseTypeId = tipo_bodega,
                        State = estado,
                        productionName = nombre_produccion
                    };

                    if (result == null)
                    {
                        result = new List<InvetoryHeaderViewModel>();
                    }
                    result.Add(invetoryHeaderViewModel);
                }
            }
            _logger.LogInformation($"result for GetListProductions {result}");
            return result;
        }

        /// <summary>
        /// Get list material by barcode
        /// </summary>
        /// <param name="barcode"></param>
        /// <returns></returns>
        public async Task<MaterialViewModel> GetMaterialByBarCode(string barcode)
        {
            string marca = string.Empty;
            string materialName = string.Empty;
            string typeElement = string.Empty;

            MaterialViewModel result = null;
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = @"select * from V_MATERIALES WHERE CODIGO_BARRAS=:barcode";

            OracleParameter opBarcode = new OracleParameter
            {
                DbType = DbType.String,
                Value = barcode,
                ParameterName = "barcode"
            };
            parameters.Add(opBarcode);

            IEnumerable<IDataRecord> records = _IOracleManagment.GetData(parameters, query);

            foreach (IDataRecord rec in records)
            {


                if (!rec.IsDBNull(rec.GetOrdinal("MATERIAL")))
                {
                    materialName = rec.GetString(rec.GetOrdinal("MATERIAL"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("TIPO_ELEMENTO")))
                {
                    typeElement = rec.GetString(rec.GetOrdinal("TIPO_ELEMENTO"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("MARCA")))
                {
                    marca = rec.GetString(rec.GetOrdinal("MARCA"));
                }

                result = new MaterialViewModel
                {
                    materialName = materialName,
                    typeElementName = typeElement,
                    marca = marca
                };
            }

            return result;
        }

        /// <summary>
        /// Get list material 
        /// </summary>
        /// <param name="warehouseType"></param>
        /// <param name="idProdction"></param>
        /// <param name="idResponsible"></param>
        /// <param name="type_element"></param>
        /// <returns></returns>
        public List<MaterialViewModel> GetMaterialsForProduction(string warehouseType, int idProdction, int idResponsible,
            int type_element, int continueInventory, int inventoryId, string fechaMovimiento = "")
        {
            _logger.LogInformation($"GetMaterialsForProduction");
            string marca = string.Empty;
            string materialName = string.Empty;
            string typeElement = string.Empty;
            int responsable_material = -1;
            string barCode = string.Empty;
            int codigo = 0;
            int produccion = -1;
            int encontrado = 0;
            string where = " WHERE V.CODIGO_TIPO_BODEGA=:COD_TIPO_BODEGA AND V.CODIGO_PRODUCCION=:COD_PRODUCCION  ";
            string orderby = " ORDER BY V.MATERIAL,V.MARCA";
            string from = string.Empty;
            decimal unitPrice = 0;

            List<MaterialViewModel> result = new List<MaterialViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();

            if (warehouseType.Equals("V"))
            {
                from = "v_bd_materiales_vestuario v inner join BD_DETALLE_INVENTARIO D ON V.CODIGO=D.BD_MTRIAL_CODIGO";
            }
            else
            {
                from = "v_bd_materiales_ambientacion  v inner join BD_DETALLE_INVENTARIO D ON V.CODIGO=D.BD_MTRIAL_CODIGO";
            }

            string query = $"select v.*,d.ENCONTRADO from {from} ";

            if (idResponsible != -1)
            {
                where += " AND V.CODIGO_RESPONSABLE=:COD_RESPONSIBLE";
            }

            if (type_element != -1)
            {
                where += " AND V.CODIGO_TIPO_ELEMENTO=:TIPO_ELEMENTO_HEADER";
            }


            if (!String.IsNullOrEmpty(fechaMovimiento))
            {
                where += " AND FECHA_MOVIMIENTO <= TO_DATE('" + fechaMovimiento + "', 'YYYY-MM-DD HH24:mi:ss')";
            }
            if (continueInventory == 1)
            {
                where += $@" AND V.CODIGO  IN (SELECT BD_MTRIAL_CODIGO FROM bd_detalle_inventario WHERE codigo_inventario= {inventoryId}) and d.codigo_inventario= {inventoryId}";
            }

            query = query + where + orderby;

            OracleParameter OpCodProduction = new OracleParameter
            {
                DbType = DbType.Int32,
                Value = idProdction,
                ParameterName = "COD_PRODUCCION"
            };
            parameters.Add(OpCodProduction);

            OracleParameter OpwarehouseType = new OracleParameter
            {
                DbType = DbType.String,
                Value = warehouseType,
                ParameterName = "COD_TIPO_BODEGA"
            };
            parameters.Add(OpwarehouseType);

            OracleParameter OpCodResponsible = new OracleParameter
            {
                DbType = DbType.Int64,
                Value = idResponsible,
                ParameterName = "COD_RESPONSIBLE"
            };
            parameters.Add(OpCodResponsible);

            OracleParameter OpCodTypeElement = new OracleParameter
            {
                DbType = DbType.String,
                Value = type_element,
                ParameterName = "TIPO_ELEMENTO_HEADER"
            };
            parameters.Add(OpCodTypeElement);

            IEnumerable<IDataRecord> records = _IOracleManagment.GetData(parameters, query);

            foreach (IDataRecord rec in records)
            {

                if (!rec.IsDBNull(rec.GetOrdinal("MATERIAL")))
                {
                    materialName = rec.GetString(rec.GetOrdinal("MATERIAL"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("TIPO_ELEMENTO")))
                {
                    typeElement = rec.GetString(rec.GetOrdinal("TIPO_ELEMENTO"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("MARCA")))
                {

                    marca = rec.GetString(rec.GetOrdinal("MARCA"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("CODIGO_BARRAS")))
                {
                    barCode = rec.GetString(rec.GetOrdinal("CODIGO_BARRAS"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("PRECIO_UNITARIO")))
                {
                    unitPrice = rec.GetDecimal(rec.GetOrdinal("PRECIO_UNITARIO"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("CODIGO")))
                {
                    codigo = rec.GetInt32(rec.GetOrdinal("CODIGO"));
                }

                if (!rec.IsDBNull(rec.GetOrdinal("CODIGO_PRODUCCION")))
                {
                    produccion = rec.GetInt32(rec.GetOrdinal("CODIGO_PRODUCCION"));
                }
                if (!rec.IsDBNull(rec.GetOrdinal("ENCONTRADO")))
                {
                    encontrado = rec.GetInt32(rec.GetOrdinal("ENCONTRADO"));
                }
                if (!rec.IsDBNull(rec.GetOrdinal("CODIGO_RESPONSABLE")))
                {
                    responsable_material = rec.GetInt32(rec.GetOrdinal("CODIGO_RESPONSABLE"));
                }

                result.Add(new MaterialViewModel
                {
                    Id = codigo,
                    materialName = materialName,
                    typeElementName = typeElement,
                    marca = marca,
                    barCode = barCode,
                    unitPrice = unitPrice,
                    productionId = produccion,
                    isReview = encontrado == 1 ? true : false,
                    ResponsableMaterial = responsable_material,
                    ListaImagenesStr = new List<string>() //getImagesByMaterial(barcode)

                });
            }
            _logger.LogInformation($"result for GetListProductions {result.Count}");
            return result;
        }

        /// <summary>
        /// Construye el select con base en los parametros enviados desde la aplicación
        /// </summary>
        /// <param name="warehouseType"></param>
        /// <param name="idProdction"></param>
        /// <param name="idResponsible"></param>
        /// <param name="type_element"></param>
        /// <param name="continueInventory"></param>
        /// <param name="inventoryId"></param>
        /// <returns></returns>
        private string GetQueryMaterialsForProduction(string warehouseType, int idProdction, int idResponsible, int type_element, int continueInventory, int inventoryId,
            string username, string fechaMovimiento = "")
        {
            _logger.LogInformation($"GetMaterialsForProduction");
            string marca = string.Empty;
            string materialName = string.Empty;
            string typeElement = string.Empty;
            string barCode = string.Empty;
            string where = " WHERE CODIGO_TIPO_BODEGA=:COD_TIPO_BODEGA AND CODIGO_PRODUCCION=:COD_PRODUCCION  ";
            string orderby = " ORDER BY MATERIAL,MARCA";
            string from = string.Empty;

            List<MaterialViewModel> result = new List<MaterialViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();

            if (warehouseType.Equals("V"))
            {
                from = "v_bd_materiales_vestuario";
            }
            else
            {
                from = "v_bd_materiales_ambientacion";
            }

            string query = $"select  codigo, {inventoryId} as CODIGO_INVENTARIO,CODIGO_RESPONSABLE,'' as  DESCRIPCION_ESTADO, 0 as ENCONTRADO, CODIGO_TIPO_ELEMENTO, FECHA_MOVIMIENTO, '{username}' as USUARIO_LOGUIN from {from} ";

            if (idResponsible != -1)
            {
                where += " AND CODIGO_RESPONSABLE=:COD_RESPONSIBLE";
            }

            if (type_element != -1)
            {
                where += " AND CODIGO_TIPO_ELEMENTO=:TIPO_ELEMENTO_HEADER";
            }
            if (continueInventory == 1)
            {
                where += $@" AND CODIGO NOT IN (SELECT BD_MTRIAL_CODIGO FROM bd_detalle_inventario WHERE codigo_inventario= {inventoryId})";
            }

            if (!String.IsNullOrEmpty(fechaMovimiento))
            {
                where += " AND FECHA_MOVIMIENTO <= TO_DATE('" + fechaMovimiento + "', 'YYYY-MM-DD HH24:mi:ss')";
            }
            query = query + where + orderby;

            return query;

        }

        /// <summary>
        /// Carga el total de material para organizar el envio parcial de materiales
        /// </summary>
        /// <param name="warehouseType"></param>
        /// <param name="idProdction"></param>
        /// <param name="idResponsible"></param>
        /// <param name="type_element"></param>
        /// <returns></returns>
        public async Task<System.Int64> GetMaterialsCountForProduction(string warehouseType, int idProdction, int idResponsible, int type_element)
        {
            _logger.LogInformation($"GetMaterialsForProduction");
            string marca = string.Empty;
            string materialName = string.Empty;
            string typeElement = string.Empty;
            string barCode = string.Empty;
            string where = " WHERE CODIGO_TIPO_BODEGA=:COD_TIPO_BODEGA AND CODIGO_PRODUCCION=:COD_PRODUCCION  ";
            string orderby = " ORDER BY MATERIAL,MARCA";
            string from = string.Empty;

            List<MaterialViewModel> result = new List<MaterialViewModel>();
            List<OracleParameter> parameters = new List<OracleParameter>();

            if (warehouseType.Equals("V"))
            {
                from = "v_bd_materiales_vestuario";
            }
            else
            {
                from = "v_bd_materiales_ambientacion";
            }

            string query = $"select  sum(cnt) as total from {from} ";

            if (idResponsible != -1)
            {
                where += " AND CODIGO_RESPONSABLE=:COD_RESPONSIBLE";
            }

            if (type_element != -1)
            {
                where += " AND TIPO_ELEMENTO=:TIPO_ELEMENTO_HEADER";
            }
            query = query + where + orderby;

            OracleParameter OpCodProduction = new OracleParameter
            {
                DbType = DbType.Int32,
                Value = idProdction,
                ParameterName = "COD_PRODUCCION"
            };
            parameters.Add(OpCodProduction);

            OracleParameter OpwarehouseType = new OracleParameter
            {
                DbType = DbType.String,
                Value = warehouseType,
                ParameterName = "COD_TIPO_BODEGA"
            };
            parameters.Add(OpwarehouseType);

            OracleParameter OpCodResponsible = new OracleParameter
            {
                DbType = DbType.Int64,
                Value = idResponsible,
                ParameterName = "COD_RESPONSIBLE"
            };
            parameters.Add(OpCodResponsible);

            OracleParameter OpCodTypeElement = new OracleParameter
            {
                DbType = DbType.String,
                Value = type_element,
                ParameterName = "TIPO_ELEMENTO_HEADER"
            };
            parameters.Add(OpCodTypeElement);


            //IEnumerable<IDataRecord> records = _IOracleManagment.GetData(parameters, query);

            using (DataSet records = _IOracleManagment.GetDataSet(parameters, query))
            {
                foreach (DataRow dr in from DataTable table in records.Tables
                                       from DataRow dr in table.Rows
                                       select dr)
                {
                    _logger.LogInformation("Materiales encontrados: " + dr["total"].ToString());
                    return Convert.ToInt64(dr["total"].ToString());
                }
            }

            _logger.LogInformation($"result for GetListProductions {result.Count}");
            return 0;
        }


        /// <summary>
        /// Carga las imagenes de cada elemento desde la base de datos
        /// </summary>
        /// <param name="idMaterial"></param>
        public List<string> getImagesByMaterial(int idMaterial)
        {
            _logger.LogInformation($"getImagesByMaterial");
            List<string> result = new List<string>();
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = "SELECT FOTO,FOTO_JAVA,BD_MATERIAL_CODIGO FROM BD_IMAGENES  WHERE BD_MATERIAL_CODIGO=:BD_MATERIAL_CODIGO ";

            OracleParameter OraMaterialCodigo = new OracleParameter(":BD_MATERIAL_CODIGO", OracleDbType.Int32, 2000, ParameterDirection.Input)
            {
                Value = idMaterial
            };
            parameters.Add(OraMaterialCodigo);

            IEnumerable<IDataRecord> records = _IOracleManagment.GetData(parameters, query);
            byte[] image;
            foreach (IDataRecord rec in records)
            {

                if (!rec["FOTO"].ToString().Equals(""))
                {
                    image = (byte[])rec["FOTO"];
                    int imageLength = image.Length;
                    string base64image = image != null ? Convert.ToBase64String(image, 0, imageLength) : string.Empty;
                    Array.Resize(ref image, 10);
                    result.Add(base64image);
                }
                else
                {
                    _logger.LogInformation($"Image´s not found for  {rec["BD_MATERIAL_CODIGO"]}");
                }
            }
            return result;
        }

        public bool UpdateStateInventory(int inventoryId, int status)
        {
            string endDate = DateTime.Now.ToString("yyyy-MM-dd HH:mm");

            OracleParameter OraCodigo = new OracleParameter(":CODIGO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraEstado = new OracleParameter(":ESTADO", OracleDbType.Int32, ParameterDirection.Input);


            string query = $@" UPDATE BD_INVENTARIO SET ESTADO=:ESTADO, FECHA_FINAL=TO_DATE('" + endDate + "', 'YYYY-MM-DD HH24:mi:ss') WHERE CODIGO=:CODIGO";

            using (OracleConnection con = new OracleConnection(_IOracleManagment.GetOracleConnectionParameters()))
            {
                con.Open();
                using (OracleCommand oraUpdate = con.CreateCommand())
                {
                    using (OracleTransaction transaction = con.BeginTransaction(IsolationLevel.ReadCommitted))
                    {
                        oraUpdate.Transaction = transaction;
                        try
                        {
                            oraUpdate.CommandText = query;
                            oraUpdate.Parameters.Add(OraEstado);
                            oraUpdate.Parameters.Add(OraCodigo);
                            OraCodigo.Value = inventoryId;
                            OraEstado.Value = status;
                            int rowCount = oraUpdate.ExecuteNonQuery();
                            transaction.Commit();
                        }
                        catch (System.Exception)
                        {
                            transaction.Rollback();
                            throw;
                        }
                    }
                }


            }
            return true;

        }

    }
}
