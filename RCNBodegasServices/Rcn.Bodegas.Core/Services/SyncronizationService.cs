using Microsoft.Extensions.Logging;
using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;

namespace Rcn.Bodegas.Core.Services
{
    public class SyncronizationService : ISyncronization
    {
        private readonly IOracleManagment _IOracleManagment;
        private readonly ILogger<SyncronizationService> _logger;
        private readonly IInventroyService _IInventroy;


        public SyncronizationService(IOracleManagment iOracleManagment, ILogger<SyncronizationService> logger, IInventroyService iInventroy)
        {
            _IOracleManagment = iOracleManagment;
            _logger = logger;
            _IInventroy = iInventroy;
        }

        public List<ProductionViewModel> GetAllListProductions()
        {
            List<ProductionViewModel> result = new List<ProductionViewModel>();

            string query = @"select * from V_PRODUCCION  ORDER BY PRODUCCION";

            DataSet records = _IOracleManagment.GetDataSet(null, query);


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
            return result;
        }

        public List<TipoElementoViewModel> GetAllListTipoElemento()
        {
            List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();

            IEnumerable<IDataRecord> records = _IOracleManagment.GetData(null, @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_ELEMENTO WHERE ESTADO='A' ORDER BY NOMBRE_TIPO ");

            result.AddRange(from IDataRecord rec in records
                            let id = rec.GetInt32(rec.GetOrdinal("CODIGO_TIPO"))
                            let name = rec.GetString(rec.GetOrdinal("NOMBRE_TIPO"))
                            select new TipoElementoViewModel
                            {
                                Id = id,
                                Name = name
                            });
            return result;
        }

        public List<TipoElementoViewModel> GetAllListTipoPrenda()
        {
            List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();

            IEnumerable<IDataRecord> records = _IOracleManagment.GetData(null, @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_PRENDA WHERE ESTADO='A' ORDER BY NOMBRE_TIPO ");

            result.AddRange(from IDataRecord rec in records
                            let id = rec.GetInt32(rec.GetOrdinal("CODIGO_TIPO"))
                            let name = rec.GetString(rec.GetOrdinal("NOMBRE_TIPO"))
                            select new TipoElementoViewModel
                            {
                                Id = id,
                                Name = name
                            });
            return result;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="offSet"></param>
        /// <param name="typeWarehouse">V)ESTUARIO A)MBIENTACIÓN</param>
        /// <returns></returns>
        public List<MaterialViewModel> GetListAllMaterial(long offSet, string warehouseType)
        {
            string warehouse;

            List<MaterialViewModel> result = new List<MaterialViewModel>();

            _logger.LogInformation("Cargando lista de materiales ");

            string query = "";
            if (warehouseType.Equals("V"))
            {
                query = $@" select * from v_bd_materiales_vestuario OFFSET {offSet} ROWS FETCH NEXT 5000 ROWS ONLY ";
            }
            else
            {
                query = $@" select * from V_BD_MATERIALES_AMBIENTACION OFFSET {offSet} ROWS FETCH NEXT 5000 ROWS ONLY ";
            }

            using (DataSet records = _IOracleManagment.GetDataSet(null, query))
            {
                IEnumerable<(DataRow dr, string materialName, string typeElement, string marca, string barCode, decimal unitPrice, string codigo, int produccion)> enumerable()
                {
                    foreach (DataTable table in records.Tables)
                    {
                        foreach ((DataRow dr, string materialName, string typeElement, string marca, string barCode, decimal unitPrice, string codigo, int produccion) in from DataRow dr in table.Rows
                                                                                                                                                                          let materialName = dr["MATERIAL"].ToString()
                                                                                                                                                                          let typeElement = dr["TIPO_ELEMENTO"].ToString().Equals("") ? "0" : dr["TIPO_ELEMENTO"].ToString()
                                                                                                                                                                          let marca = dr["MARCA"].ToString().Equals("") ? "" : dr["MARCA"].ToString()
                                                                                                                                                                          let barCode = dr["CODIGO_BARRAS"].ToString()
                                                                                                                                                                          let unitPrice = Convert.ToDecimal(dr["PRECIO_UNITARIO"].ToString().Equals("") ? "0" : dr["PRECIO_UNITARIO"].ToString())
                                                                                                                                                                          let codigo = dr["CODIGO"].ToString().Equals("") ? "0" : dr["CODIGO"].ToString()
                                                                                                                                                                          let produccion = Convert.ToInt32(dr["CODIGO_PRODUCCION"].ToString())
                                                                                                                                                                          select (dr, materialName, typeElement, marca, barCode, unitPrice, codigo, produccion))
                        {
                            yield return (dr, materialName, typeElement, marca, barCode, unitPrice, codigo, produccion);
                        }
                    }
                }

                foreach ((DataRow dr, string materialName, string typeElement, string marca, string barCode, decimal unitPrice, string codigo, int produccion) in enumerable())
                {
                    warehouse = dr["CODIGO_TIPO_BODEGA"].ToString();
                    result.Add(new MaterialViewModel
                    {
                        wareHouseId = warehouse,
                        productionId = produccion,
                        codigo = codigo,
                        materialName = materialName,
                        typeElementName = typeElement,
                        marca = marca,
                        barCode = barCode,
                        unitPrice = unitPrice,
                        ListaImagenesStr = new List<string>()// _IInventroy.getImagesByMaterial(Convert.ToInt32(codigo))
                        //ListaImagenesStr = _IInventroy.getImagesByMaterial(Convert.ToInt32(codigo))
                    });
                }
            }

            _logger.LogInformation("Materiales encontrados: " + result.Count.ToString());
            return result;
        }

        public long GetCountMaterial(string warehouseType)
        {

            _logger.LogInformation("Revisanbdo cantidad de materiales a consultar ");

            string query;
            if (warehouseType.Equals("A"))
            {
                query = "select COUNT(*) as total from V_BD_MATERIALES_AMBIENTACION ";
            }
            else
            {
                query = "select COUNT(*) as total from v_bd_materiales_vestuario ";
            }

            using (DataSet records = _IOracleManagment.GetDataSet(null, query))
            {
                foreach (DataRow dr in from DataTable table in records.Tables
                                       from DataRow dr in table.Rows
                                       select dr)
                {
                    _logger.LogInformation("Materiales encontrados: " + dr["total"].ToString());
                    return Convert.ToInt64(dr["total"].ToString());
                }
            }

            return 0;

        }

        public List<ResponsibleViewModel> GetListAllResponsible()
        {
#if DEBUG
            _logger.LogInformation("Debug mode... ");
#else
        _logger.LogInformation("release mode... ");
#endif
            List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();
            _logger.LogInformation("Ejecutando servicio GetListResponsable ");
            List<OracleParameter> parameters = new List<OracleParameter>();

            string query = @"SELECT M.pa_tercero_codigo  Codigo_Responsable,
                       (  Select Nombre 
                          From GN_TERCERO
                          Where codigo = M.pa_tercero_codigo) Nombre_Responsable,M.BD_Ubccion_Codigo Codigo_Produccion,(
                            Select NOMBRE 
                            From BD_UBICACION 
                            Where Codigo = M.bd_ubccion_codigo) Produccion 
                    FROM bd_movimiento_material M
                    GROUP BY M.pa_tercero_codigo, M.BD_Ubccion_Codigo";

            _logger.LogInformation("Query " + query);


            using (DataSet records = _IOracleManagment.GetDataSet(null, query))
            {
                foreach (DataTable table in records.Tables)
                {
                    _logger.LogInformation("Iterando registros");
                    result.AddRange(from DataRow dr in table.Rows
                                    let id = dr["CODIGO_RESPONSABLE"].ToString()
                                    let name = dr["NOMBRE_RESPONSABLE"].ToString()
                                    let codigoProduccion = dr["CODIGO_PRODUCCION"].ToString()
                                    select new ResponsibleViewModel
                                    {
                                        Id = id,
                                        Name = name,
                                        CodigoProduccion = codigoProduccion
                                    });
                }
            }

            _logger.LogInformation("Registros retornados " + new List<ResponsibleViewModel>().Count.ToString());
            return result;
        }

        public List<WareHouseViewModel> GetListAllWarehouseUser()
        {
            List<WareHouseViewModel> result = new List<WareHouseViewModel>();
            _logger.LogInformation("Query " + @"select * from V_TIPO_BODEGA");

            using (DataSet records = _IOracleManagment.GetDataSet(null, @"select * from V_TIPO_BODEGA"))
            {
                foreach (DataTable table in records.Tables)
                {
                    _logger.LogInformation("Iterando registros");

                    result.AddRange(from DataRow dr in table.Rows
                                    let id = dr["CODIGO"].ToString()
                                    let tipo_bodega = dr["TIPO_BODEGA"].ToString()
                                    select new WareHouseViewModel
                                    {
                                        Id = id,
                                        WareHouseName = tipo_bodega
                                    });
                }
            }

            return result;
        }

        public List<ResponsibleViewModel> GetAllListWarehouseUser()
        {
            List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();



            using (DataSet records = _IOracleManagment.GetDataSet(null, @"select unique codigo_responsable codigo_responsable,nombre_responsable, DECODE(codigo_produccion,1,'V','A')as TIPO_BODEGA
                  from V_RESPONSABLE
                  where codigo_produccion in (1,2)  
                  ORDER BY NOMBRE_RESPONSABLE"))
            {
                foreach (DataTable table in records.Tables)
                {
                    _logger.LogInformation("Iterando registros");

                    result.AddRange(from DataRow dr in table.Rows
                                    let id = dr["CODIGO_RESPONSABLE"].ToString()
                                    let name = dr["NOMBRE_RESPONSABLE"].ToString()
                                    let tipoBodega = dr["TIPO_BODEGA"].ToString()
                                    select new ResponsibleViewModel
                                    {
                                        Id = id,
                                        Name = name,
                                        TipoBodega = tipoBodega
                                    });
                }
            }

            return result;
        }
    }
}
