﻿using Microsoft.Extensions.Logging;
using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;

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

    public async Task<List<ProductionViewModel>> GetAllListProductionsAsync()
    {
      List<ProductionViewModel> result = new List<ProductionViewModel>();

      string query = @"select * from V_PRODUCCION  ORDER BY PRODUCCION";

      var records = _IOracleManagment.GetDataSet(null, query);


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

    public async Task<List<TipoElementoViewModel>> GetAllListTipoElementoAsync()
    {
      List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      string query = string.Empty;


      query = @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_ELEMENTO WHERE ESTADO='A' ORDER BY NOMBRE_TIPO ";


      var records = _IOracleManagment.GetData(null, query);

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

    public async Task<List<TipoElementoViewModel>> GetAllListTipoPrendaAsync()
    {
      List<TipoElementoViewModel> result = new List<TipoElementoViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      string query = string.Empty;


      query = @"select CODIGO_TIPO, NOMBRE_TIPO from BD_TIPO_PRENDA WHERE ESTADO='A' ORDER BY NOMBRE_TIPO ";


      var records = _IOracleManagment.GetData(null, query);

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

    public async Task<List<MaterialViewModel>> GetListAllMaterialAsync(Int64 offSet)
    {
      string marca = string.Empty;
      string materialName = string.Empty;
      string typeElement = string.Empty;
      string barCode = string.Empty;
      string codigo = string.Empty;
      decimal unitPrice = 0;

      List<MaterialViewModel> result = new List<MaterialViewModel>();
      List<OracleParameter> parameters = new List<OracleParameter>();
      var query = $@"select * from BD_MATERIAL  WHERE ESTADO='A' OFFSET {offSet} ROWS FETCH NEXT 3000 ROWS ONLY";

      _logger.LogInformation("Cargando lista de materiales ");

      var records = _IOracleManagment.GetDataSet(null, query);

      foreach (DataTable table in records.Tables)
      {

        foreach (DataRow dr in table.Rows)
        {
          materialName = dr["DESCRIPCION"].ToString();
          typeElement = dr["TIPO_ELEMENTO"].ToString();
          marca = dr["MARCA"].ToString();
          barCode = dr["CODIGO_BARRAS"].ToString();
          unitPrice = Convert.ToDecimal(dr["VALOR_COMPRA"].ToString().Equals("") ? "0" : dr["VALOR_COMPRA"].ToString());
          codigo = dr["CODIGO"].ToString().Equals("") ? "0" : dr["CODIGO"].ToString();

          result.Add(new MaterialViewModel
          {
            codigo = codigo,
            materialName = materialName,
            typeElementName = typeElement,
            marca = marca,
            barCode = barCode,
            unitPrice = unitPrice,
            ListaImagenesStr = new List<string>()//_IInventroy.getImagesByMaterial(barcode)

          });

        }
      }

      _logger.LogInformation("Materiales encontrados: " + result.Count.ToString());
      return result;
    }

    public async Task<Int64> GetCountMaterialAsync()
    {


      var query = @"select COUNT(*) as total from bd_material WHERE ESTADO='A'";

      _logger.LogInformation("Revisanbdo cantidad de materiales a consultar ");

      var records = _IOracleManagment.GetDataSet(null, query);

      foreach (DataTable table in records.Tables)
      {
        foreach (DataRow dr in table.Rows)
        {
          _logger.LogInformation("Materiales encontrados: " + dr["total"].ToString());
          return Convert.ToInt64(dr["total"].ToString());
        }


      }
      return 0;

    }

    public async Task<List<ResponsibleViewModel>> GetListAllResponsibleAsync()
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
                          Where codigo = M.pa_tercero_codigo) Nombre_Responsable,M.BD_Ubccion_Codigo Codigo_Produccion,(
                            Select NOMBRE 
                            From BD_UBICACION 
                            Where Codigo = M.bd_ubccion_codigo) Produccion 
                    FROM bd_movimiento_material M
                    GROUP BY M.pa_tercero_codigo, M.BD_Ubccion_Codigo";

      _logger.LogInformation("Query " + query);


      var records = _IOracleManagment.GetDataSet(parameters, query);

      foreach (DataTable table in records.Tables)
      {
        _logger.LogInformation("Iterando registros");
        foreach (DataRow dr in table.Rows)
        {
          Int64 id = Convert.ToInt64(dr["CODIGO_RESPONSABLE"].ToString());
          string name = dr["NOMBRE_RESPONSABLE"].ToString();
          string codigoProduccion = dr["CODIGO_PRODUCCION"].ToString();

          result.Add(new ResponsibleViewModel
          {
            Id = id,
            Name = name,
            CodigoProduccion=codigoProduccion
          });

        }
      }

      _logger.LogInformation("Registros retornados " + result.Count.ToString());
      return result;
    }

    public async Task<List<WareHouseViewModel>> GetListAllWarehouseUserAsync()
    {
      List<WareHouseViewModel> result = new List<WareHouseViewModel>();
      var query = @"select * from V_TIPO_BODEGA";
      _logger.LogInformation("Query " + query);

      var records = _IOracleManagment.GetDataSet(null, query);

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

    public async Task<List<ResponsibleViewModel>> GetAllListWarehouseUserAsync()
    {
      List<ResponsibleViewModel> result = new List<ResponsibleViewModel>();
      
      var query = @"select unique codigo_responsable codigo_responsable,nombre_responsable, DECODE(codigo_produccion,1,'V','A')as TIPO_BODEGA
                  from V_RESPONSABLE
                  where codigo_produccion in (1,2)  
                  ORDER BY NOMBRE_RESPONSABLE";

   
      var records = _IOracleManagment.GetDataSet(null, query);

      foreach (DataTable table in records.Tables)
      {
        _logger.LogInformation("Iterando registros");
        foreach (DataRow dr in table.Rows)
        {
          int id = Convert.ToInt32(dr["CODIGO_RESPONSABLE"].ToString());
          string name = dr["NOMBRE_RESPONSABLE"].ToString();
          string tipoBodega= dr["TIPO_BODEGA"].ToString();

          result.Add(new ResponsibleViewModel
          {
            Id = id,
            Name = name,
            TipoBodega=tipoBodega
          });

        }
      }

      return result;
    }
  }
}
