using Oracle.ManagedDataAccess.Client;
using System.Collections.Generic;
using System.Data;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface IOracleManagment
  {
    string GetOracleConnectionParameters();
    IEnumerable<IDataRecord> GetData(List<OracleParameter>parameters, string query);  

    OracleParameter BuildParameter(System.Data.DbType parameter, string value, string parameterName);

  }
}
