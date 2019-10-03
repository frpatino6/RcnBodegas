using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface IInventroyService
  {
    Task<List<ProductionViewModel>> GetListProductions(string wareHouse);

    Task<List<ResponsibleViewModel>> GetListResponsible(string wareHouse, string production);

    Task<List<ResponsibleViewModel>> GetListWarehouseUserAsync(string tipoBodega);

    Task<List<TipoElementoViewModel>> GetListTipoElemento(string wareHouse);

    Task<MaterialViewModel> GetMaterialByBarCode(string barcode);

    Task<List<MaterialViewModel>> GetMaterialsForProduction(string warehouseType, int productionId, int responsibleId, int type_element);

    Task<bool> CreateInconsistency(string warehouseType, string productionId, int responsibleId);


  }
}
