using Rcn.Bodegas.Core.ViewModel;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Interfaces
{
  public interface IInventroyService
  {
    Task<List<ProductionViewModel>> GetListProductions(string wareHouse);

    Task<List<ResponsibleViewModel>> GetListResponsible(string wareHouse, string production);

    Task<List<TipoElementoViewModel>> GetListTipoElemento(string wareHouse);

    Task<MaterialViewModel> GetMaterialByBarCode(string barcode);

    Task<List<MaterialViewModel>> GetMaterialsForProduction(string warehouseType, string productionId, int responsibleId);
  }
}
