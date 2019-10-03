using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Oracle.ManagedDataAccess.Client;
using Rcn.Bodegas.Core.Interfaces;
using System.Collections.Generic;
using System.Data;

namespace Rcn.Bodegas.Core.Helpers
{
  public class OracleManagment : IOracleManagment
  {
    private readonly ILogger<OracleManagment> _logger;
    private readonly IConfiguration _IConfiguration;

    public OracleManagment(IConfiguration configuration, ILogger<OracleManagment> logger)
    {
      this._IConfiguration = configuration;
      this._logger = logger;
    }
    public OracleParameter BuildParameter(System.Data.DbType type, string value, string parameterName)
    {
      OracleParameter parameter = new OracleParameter();
      parameter.DbType = type;
      parameter.Value = value;
      parameter.ParameterName = parameterName;

      return parameter;

    }

    public IEnumerable<IDataRecord> GetData(List<OracleParameter> parameters, string query)
    {

      using (OracleConnection con = new OracleConnection(GetOracleConnectionParameters()))
      {
        using (OracleCommand cmd = con.CreateCommand())
        {

          _logger.LogInformation("Abriendo conexión a oracle ");
          con.Open();
          cmd.BindByName = true;

          ///Carga los parametros de la consulta
          _logger.LogInformation("Cargando parametros de la consulta");
          if (parameters != null)
            foreach (var item in parameters)
            {
              cmd.Parameters.Add(item);
            }
          //Configura la instrucción sql
          _logger.LogInformation("Configurando la instrucción sql");
          cmd.CommandText = query;

          //Ejectua la consulta
          _logger.LogInformation("Ejecutando instrucción sql");
          using (OracleDataReader reader = cmd.ExecuteReader())
          {
            while (reader.Read())
            {
              _logger.LogInformation("Enviando resultado a la capa de servicios");
              yield return reader;
            }
          }
        }
      }
    }

    public string GetOracleConnectionParameters()
    {
      var userNameDatabase = _IConfiguration.GetSection("OracleConfig").GetSection("userId").Value;
      var pws = _IConfiguration.GetSection("OracleConfig").GetSection("pws").Value;
      var datasource = _IConfiguration.GetSection("OracleConfig").GetSection("dataSource").Value;


      string conString = $"User Id={userNameDatabase};Password={pws};" +
      //How to connect to an Oracle DB without SQL*Net configuration file
      //  also known as tnsnames.ora.
      $"Data Source={datasource}";

      _logger.LogInformation($"Data Source={datasource} conección {conString}");
      return conString;
    }
  }
}
