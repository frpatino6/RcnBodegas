using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.IO;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
    public class WareHouseServices : IWareHouseServices
    {
        private readonly IOracleManagment _IOracleManagment;
        private readonly IConfiguration _IConfiguration;
        private readonly ILogger<WareHouseServices> _logger;

        #region Public Methods
        public WareHouseServices(IOracleManagment oracleManagment, IConfiguration configuration, ILogger<WareHouseServices> logger)
        {
            _IOracleManagment = oracleManagment;
            _IConfiguration = configuration;
            _logger = logger;
        }

        public async Task<int> CreateMaterialWarehouse(List<MaterialViewModel> listNewMaterial, string warehouseid)
        {
            string dateLegalization;
            string adminMaterialTypeId = _IConfiguration.GetSection("AppParameters").GetSection("codigo_tipo_elemento_administrativo").Value;
            int rowCount = 0;
            int numeroRecepcion = 0;
            OracleParameter OraBarCode = new OracleParameter(":CODIGO_BARRAS", OracleDbType.Varchar2, 20, ParameterDirection.Input);
            OracleParameter OraDescripcion = new OracleParameter(":DESCRIPCION", OracleDbType.Varchar2, 2000, ParameterDirection.Input);
            OracleParameter OraTipoBodega = new OracleParameter(":TIPO_BODEGA", OracleDbType.Varchar2, 1, ParameterDirection.Input);
            OracleParameter OraUbicacionActua = new OracleParameter(":BD_UBCCION_ACTUAL", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraEstado = new OracleParameter(":ESTADO", OracleDbType.Varchar2, 1, ParameterDirection.Input);
            OracleParameter OraEmpresaCodigo = new OracleParameter(":AD_EMPRESA_CODIGO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraUbicacionCodigo = new OracleParameter(":BD_UBCCION_CODIGO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraUsuarioCreacion = new OracleParameter(":USU_CREACION", OracleDbType.Varchar2, 20, ParameterDirection.Input);
            OracleParameter OraValorCompra = new OracleParameter(":VALOR_COMPRA", OracleDbType.Decimal, ParameterDirection.Input);
            OracleParameter OraValorMateria = new OracleParameter(":VALOR_MATERIA", OracleDbType.Decimal, ParameterDirection.Input);
            OracleParameter OraTerceroActual = new OracleParameter(":PA_TERCERO_ACTUAL", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraElementType = new OracleParameter(":TIPO_ELEMENTO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraPrendaType = new OracleParameter(":TIPO_PRENDA", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraMarca = new OracleParameter(":MARCA", OracleDbType.Varchar2, 48, ParameterDirection.Input);
            OracleParameter OraUbicacionRecibido = new OracleParameter(":BD_UBICACION_RECIBIDO", OracleDbType.Varchar2, 48, ParameterDirection.Input);
            OracleParameter OraTerceroRecibido = new OracleParameter(":PA_TERCERO_RECIBIDO", OracleDbType.Int32, ParameterDirection.Input);
            OracleParameter OraFechaCompra = new OracleParameter(":FECHA_COMPRA", OracleDbType.Date, ParameterDirection.Input);


            OracleParameter OraCodigoElemento = new OracleParameter("new_id", OracleDbType.Int32)
            {
                Direction = ParameterDirection.Output
            };

            numeroRecepcion = getDocumentNumber(warehouseid);
            dateLegalization = listNewMaterial[0].saleDate.ToString("yyyy-MM-dd");
            String currentBarcode = "";

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
                            if (numeroRecepcion > 0)
                            {

                                foreach (MaterialViewModel newMaterial in listNewMaterial)
                                {
                                    oraUpdate.Parameters.Clear();
                                    SetParameters(OraBarCode, OraDescripcion, OraTipoBodega, OraUbicacionActua, OraEstado, OraEmpresaCodigo, OraUbicacionCodigo, OraUsuarioCreacion, OraValorCompra, OraValorMateria, OraTerceroActual, OraElementType, OraMarca, OraUbicacionRecibido, OraTerceroRecibido, OraCodigoElemento, oraUpdate);

                                    if (newMaterial.isAdmin)
                                    {
                                        oraUpdate.Parameters.Remove(OraBarCode);
                                        oraUpdate.Parameters.Remove(OraUbicacionActua);
                                        oraUpdate.Parameters.Remove(OraTerceroActual);
                                        oraUpdate.CommandText = SetSqlQueryAdmin(warehouseid, dateLegalization, numeroRecepcion);
                                    }
                                    else
                                    {
                                        oraUpdate.CommandText = SetSqlQuery(warehouseid, dateLegalization, numeroRecepcion);
                                    }

                                    OraBarCode.Value = OraBarCode.Value == null ? "" : newMaterial.barCode;
                                    currentBarcode = newMaterial.barCode;
                                    OraDescripcion.Value = newMaterial.materialName;
                                    OraTipoBodega.Value = newMaterial.wareHouseId;
                                    OraUbicacionActua.Value = 1;
                                    OraEstado.Value = "A";
                                    OraEmpresaCodigo.Value = 1;
                                    OraUbicacionCodigo.Value = 1;
                                    OraUsuarioCreacion.Value = "BODEGAS";
                                    OraValorCompra.Value = newMaterial.unitPrice;
                                    OraValorMateria.Value = newMaterial.purchaseValue;
                                    OraTerceroActual.Value = newMaterial.terceroActual;
                                    OraElementType.Value = newMaterial.typeElementId;
                                    OraMarca.Value = newMaterial.marca;
                                    OraUbicacionRecibido.Value = newMaterial.productionId;
                                    OraTerceroRecibido.Value = newMaterial.legalizedBy;
                                    rowCount = oraUpdate.ExecuteNonQuery();

                                    if (newMaterial.ListaImagenesStr != null && newMaterial.ListaImagenesStr.Count > 0)
                                    {
                                        InsertImagesByMaterial(OraCodigoElemento.Value.ToString(), newMaterial.ListaImagenesStr, con, transaction, newMaterial.isAdmin);
                                    }
                                }
                            }

                            transaction.Commit();


                        }
                        catch (System.Exception ex)
                        {
                            transaction.Rollback();
                            throw new System.Exception($"Error creando el material con código de barras {currentBarcode}. {ex.Message}");
                        }
                    }
                }
            }
            return numeroRecepcion;

        }

        private static void SetParameters(OracleParameter OraBarCode, OracleParameter OraDescripcion, OracleParameter OraTipoBodega, OracleParameter OraUbicacionActua, OracleParameter OraEstado, OracleParameter OraEmpresaCodigo, OracleParameter OraUbicacionCodigo, OracleParameter OraUsuarioCreacion, OracleParameter OraValorCompra, OracleParameter OraValorMateria, OracleParameter OraTerceroActual, OracleParameter OraElementType, OracleParameter OraMarca, OracleParameter OraUbicacionRecibido, OracleParameter OraTerceroRecibido, OracleParameter OraCodigoElemento, OracleCommand oraUpdate)
        {
            oraUpdate.Parameters.Add(OraBarCode);
            oraUpdate.Parameters.Add(OraDescripcion);
            oraUpdate.Parameters.Add(OraTipoBodega);
            oraUpdate.Parameters.Add(OraUbicacionActua);
            oraUpdate.Parameters.Add(OraEstado);
            oraUpdate.Parameters.Add(OraEmpresaCodigo);
            oraUpdate.Parameters.Add(OraUbicacionCodigo);
            oraUpdate.Parameters.Add(OraUsuarioCreacion);
            oraUpdate.Parameters.Add(OraValorCompra);
            oraUpdate.Parameters.Add(OraValorMateria);
            oraUpdate.Parameters.Add(OraTerceroActual);
            oraUpdate.Parameters.Add(OraElementType);
            oraUpdate.Parameters.Add(OraMarca);
            oraUpdate.Parameters.Add(OraUbicacionRecibido);
            oraUpdate.Parameters.Add(OraTerceroRecibido);
            oraUpdate.Parameters.Add(OraCodigoElemento);
        }

        #endregion

        #region Private Methods
        private void InsertImagesByMaterial(string codigoMaterial, List<string> listImages, OracleConnection con, OracleTransaction transaction, bool isAdmin)
        {
            string query = string.Empty;
            int rowCount = 0;
            OracleParameter OraEmpresae = new OracleParameter(":AD_EMPRESA_CODIGO", OracleDbType.Int32, 20, ParameterDirection.Input);
            OracleParameter OraMaterialCodigo = new OracleParameter(":BD_MATERIAL_CODIGO", OracleDbType.Int32, 2000, ParameterDirection.Input);
            OracleParameter OraConsecutivo = new OracleParameter(":CONSECUTIVO", OracleDbType.Int32, 1, ParameterDirection.Input);
            OracleParameter OraDescripcion = new OracleParameter(":DESCRIPCION", OracleDbType.Varchar2, ParameterDirection.Input);
            OracleParameter OraArchivo = new OracleParameter(":ARCHIVO", OracleDbType.Varchar2, 1, ParameterDirection.Input);
            OracleParameter OraFoto = new OracleParameter(":FOTO", OracleDbType.Blob, ParameterDirection.Input);

            if (!isAdmin)
            {
                query = $@" INSERT INTO BD_IMAGENES (AD_EMPRESA_CODIGO,BD_MATERIAL_CODIGO,CONSECUTIVO,DESCRIPCION,ARCHIVO,FOTO,FOTO_JAVA)
                                          VALUES(:AD_EMPRESA_CODIGO,:BD_MATERIAL_CODIGO,:CONSECUTIVO,:DESCRIPCION,:ARCHIVO,:FOTO,:FOTO)";
            }
            else
            {
                query = $@" INSERT INTO BD_IMAGENES_ADMIN (AD_EMPRESA_CODIGO,BD_MATERIAL_CODIGO,CONSECUTIVO,DESCRIPCION,ARCHIVO,FOTO,FOTO_JAVA)
                                          VALUES(:AD_EMPRESA_CODIGO,:BD_MATERIAL_CODIGO,:CONSECUTIVO,:DESCRIPCION,:ARCHIVO,:FOTO,:FOTO)";
            }

            OracleCommand oraUpdate = con.CreateCommand();
            int consecutivo = 1;

            oraUpdate.CommandText = query;
            oraUpdate.Parameters.Add(OraEmpresae);
            oraUpdate.Parameters.Add(OraMaterialCodigo);
            oraUpdate.Parameters.Add(OraConsecutivo);
            oraUpdate.Parameters.Add(OraDescripcion);
            oraUpdate.Parameters.Add(OraArchivo);
            oraUpdate.Parameters.Add(OraFoto);

            Image imageForSave;

            foreach (string item in listImages)
            {

                byte[] bytesImage = Convert.FromBase64String(item);

                using (MemoryStream mStream = new MemoryStream(bytesImage))
                {
                    imageForSave = Image.FromStream(mStream);
                }


                OraEmpresae.Value = 1;
                OraMaterialCodigo.Value = codigoMaterial;
                OraConsecutivo.Value = consecutivo;
                OraDescripcion.Value = string.Empty;
                OraArchivo.Value = string.Empty;
                OraFoto.Value = bytesImage;
                consecutivo++;
                rowCount = oraUpdate.ExecuteNonQuery();
            }


        }

        private static string SetSqlQuery(string warehouseid, string dateLegalization, int numeroRecepcion)
        {
            string query;
            if (warehouseid.Equals("V"))
            {
                query = $@"Insert into BD_MATERIAL (CODIGO,CODIGO_BARRAS,DESCRIPCION,TIPO_BODEGA,BD_UBCCION_ACTUAL,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,VALOR_MATERIA,PA_TERCERO_ACTUAL,TIPO_PRENDA,MARCA,VALOR_RETENCION,ASIGNACION_BARRAS,BD_UBICACION_RECIBIDO,PA_TERCERO_RECIBIDO,FECHA_COMPRA,NUMERO_DOC_RECEPCION)
                 VALUES(BD_MTRIAL_SEQ.nextval, :CODIGO_BARRAS, :DESCRIPCION, :TIPO_BODEGA, :BD_UBCCION_ACTUAL, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, :VALOR_MATERIA,:PA_TERCERO_ACTUAL,:TIPO_ELEMENTO,:MARCA,0,'S',:BD_UBICACION_RECIBIDO,:PA_TERCERO_RECIBIDO,TO_DATE('" + dateLegalization + "', 'YYYY-MM-DD HH:mi:ss')," + numeroRecepcion + ") returning CODIGO into :new_id";
            }
            else
            {
                query = $@"Insert into BD_MATERIAL (CODIGO,CODIGO_BARRAS,DESCRIPCION,TIPO_BODEGA,BD_UBCCION_ACTUAL,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,VALOR_MATERIA,PA_TERCERO_ACTUAL,TIPO_ELEMENTO,MARCA,VALOR_RETENCION,ASIGNACION_BARRAS,BD_UBICACION_RECIBIDO,PA_TERCERO_RECIBIDO,FECHA_COMPRA,NUMERO_DOC_RECEPCION)
                 VALUES(BD_MTRIAL_SEQ.nextval, :CODIGO_BARRAS, :DESCRIPCION, :TIPO_BODEGA, :BD_UBCCION_ACTUAL, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, :VALOR_MATERIA,:PA_TERCERO_ACTUAL,:TIPO_ELEMENTO,:MARCA,0,'S',:BD_UBICACION_RECIBIDO,:PA_TERCERO_RECIBIDO,TO_DATE('" + dateLegalization + "', 'YYYY-MM-DD HH:mi:ss')," + numeroRecepcion + ") returning CODIGO into :new_id";
            }

            return query;
        }

        private static string SetSqlQueryAdmin(string warehouseid, string dateLegalization, int numeroRecepcion)
        {
            string query;
            if (warehouseid.Equals("V"))
            {
                query = $@"Insert into BD_MATERIAL_ADMIN (CODIGO,DESCRIPCION,TIPO_BODEGA,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,VALOR_MATERIA,TIPO_PRENDA,MARCA,BD_UBICACION_RECIBIDO,PA_TERCERO_RECIBIDO,FECHA_COMPRA,NUMERO_DOC_RECEPCION)
                 VALUES((SELECT NVL(MAX(codigo),0) + 1 FROM BD_MATERIAL_ADMIN),  :DESCRIPCION, :TIPO_BODEGA, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, :VALOR_MATERIA,:TIPO_ELEMENTO,:MARCA,:BD_UBICACION_RECIBIDO,:PA_TERCERO_RECIBIDO,TO_DATE('" + dateLegalization + "', 'YYYY-MM-DD HH:mi:ss')," + numeroRecepcion + ") returning CODIGO into :new_id";
            }
            else
            {
                query = $@"Insert into BD_MATERIAL_ADMIN (CODIGO,DESCRIPCION,TIPO_BODEGA,FEC_CREACION,ESTADO,AD_EMPRESA_CODIGO,BD_UBCCION_CODIGO,USU_CREACION,VALOR_COMPRA,VALOR_MATERIA,TIPO_ELEMENTO,MARCA,BD_UBICACION_RECIBIDO,PA_TERCERO_RECIBIDO,FECHA_COMPRA,NUMERO_DOC_RECEPCION)
                 VALUES((SELECT NVL(MAX(codigo),0) + 1 FROM BD_MATERIAL_ADMIN),  :DESCRIPCION, :TIPO_BODEGA, SYSDATE, :ESTADO, :AD_EMPRESA_CODIGO, :BD_UBCCION_CODIGO, :USU_CREACION, :VALOR_COMPRA, :VALOR_MATERIA,:TIPO_ELEMENTO,:MARCA,:BD_UBICACION_RECIBIDO,:PA_TERCERO_RECIBIDO,TO_DATE('" + dateLegalization + "', 'YYYY-MM-DD HH:mi:ss')," + numeroRecepcion + ") returning CODIGO into :new_id";
            }

            return query;
        }

        private int getDocumentNumber(string tipoBodega)
        {
            int id = 0;
            List<OracleParameter> parameters = new List<OracleParameter>();
            string query = @"SELECT NVL(MAX(numero_doc_recepcion),0) + 1 as NUMERO_DOC
                    FROM V_BD_NUMERO_DOCUMENTO
                    WHERE tipo_bodega = :TIPO_BODEGA";

            OracleParameter opCodigoMaterial = new OracleParameter
            {
                DbType = DbType.String,
                Value = tipoBodega,
                ParameterName = ":TIPO_BODEGA"
            };
            parameters.Add(opCodigoMaterial);


            DataSet records = _IOracleManagment.GetDataSet(parameters, query);

            foreach (DataTable table in records.Tables)
            {
                _logger.LogInformation("Iterando registros");
                foreach (DataRow dr in table.Rows)
                {
                    id = Convert.ToInt32(dr["NUMERO_DOC"].ToString());

                }
            }
            //foreach (IDataRecord rec in records)
            //{
            //  id = rec.GetInt32(rec.GetOrdinal("NUMERO_DOC"));
            //}
            return id;
        }

        public async Task<List<WareHouseViewModel>> GetListWareHouseByUser(string userName, int companyId)
        {
            List<WareHouseViewModel> result = new List<WareHouseViewModel>();
            string query = @"select * from V_TIPO_BODEGA";
            _logger.LogInformation("Query " + query);

            DataSet records = _IOracleManagment.GetDataSet(null, query);

            foreach (DataTable table in records.Tables)
            {
                _logger.LogInformation("Iterando registros");
                foreach (DataRow dr in table.Rows)
                {
                    string id = dr["CODIGO"].ToString();
                    string tipo_bodega = dr["TIPO_BODEGA"].ToString();

                    result.Add(new WareHouseViewModel
                    {
                        Id = id,
                        WareHouseName = tipo_bodega
                    });

                }
            }

            return result;

        }
        #endregion

    }
}