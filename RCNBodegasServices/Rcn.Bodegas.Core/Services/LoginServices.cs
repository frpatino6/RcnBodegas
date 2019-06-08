using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
  public class LoginServices : ILoginServices
  {
    public async Task<UserViewModel> GetUserAsync(string userName)
    {
      return new UserViewModel { Id = 1, Name = "UserTest", RoleId = 121, RoleName = "perfil admin", UserName = "frpatino6" };
    }
  }
}
