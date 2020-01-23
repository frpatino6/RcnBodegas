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

        Task<System.Int64> GetMaterialsCountForProduction(string warehouseType, int idProdction, int idResponsible, int type_element);

        Task<bool> CreateInconsistency(string warehouseType, string productionId, int responsibleId);

        Task<List<InvetoryHeaderViewModel>> GetPendingInventoryByUser(string userName);

        int CreateInventoryHeader(InvetoryHeaderViewModel invetoryViewModel);

        int CreateInventoryDetail(InventoryDetailViewModel inventoryDetailViewModel, int status);

        List<string> getImagesByMaterial(int idMaterial);

    }
}
