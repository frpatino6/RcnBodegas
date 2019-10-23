using Rcn.Bodegas.Core.ViewModel;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface ISyncronization
  {
    Task<List<ProductionViewModel>> GetAllListProductionsAsync();


    Task<List<ResponsibleViewModel>> GetListAllResponsibleAsync();

    Task<List<TipoElementoViewModel>> GetAllListTipoElementoAsync();

    Task<List<TipoElementoViewModel>> GetAllListTipoPrendaAsync();

    Task<List<WareHouseViewModel>> GetListAllWarehouseUserAsync();

    Task<Int64> GetCountMaterialAsync();

    Task<List<MaterialViewModel>> GetListAllMaterialAsync(Int64 offSet);

    Task<List<ResponsibleViewModel>> GetAllListWarehouseUserAsync();
  }
}
