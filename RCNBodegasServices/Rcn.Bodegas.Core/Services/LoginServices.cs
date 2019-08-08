using Microsoft.Extensions.Configuration;
using Rcn.Bodegas.Core.Interfaces;
using Rcn.Bodegas.Core.ViewModel;
using System;
using System.DirectoryServices.AccountManagement;
using System.Threading.Tasks;

namespace Rcn.Bodegas.Core.Services
{
  public class LoginServices : ILoginServices
  {


    private readonly IConfiguration _IConfiguration;
    private string sDefaultOU;
    private string passwordAdmin;
    private string userNameAdmin;
    private string sDomain;

    public LoginServices(IConfiguration iConfiguration)
    {
      _IConfiguration = iConfiguration;
      sDefaultOU = _IConfiguration.GetSection("DomainConfigFS").GetSection("sDefaultOU").Value;
      passwordAdmin = _IConfiguration.GetSection("DomainConfigFS").GetSection("passwordAdmin").Value;
      userNameAdmin = _IConfiguration.GetSection("DomainConfigFS").GetSection("userNameAdmin").Value;
      sDomain = _IConfiguration.GetSection("DomainConfigFS").GetSection("sDomain").Value;
   
    }

    public async Task<UserViewModel> GetUserAsync(string userName, string password)
    {
      using (var ctx = new PrincipalContext(ContextType.Domain, sDomain, userName, password))
      {
        var result = ctx.ValidateCredentials(userName, password);//Valida el usuario y pws

        if (result)//Si lo encuentra, llama el servicio http que busca el registro el accesstoken el usario para la plataforma que está utilizando
        {
          return new UserViewModel { Id = 1, Name = "UserTest", RoleId = 121, RoleName = "perfil admin", UserName = userName };
        }
        else
        {
          throw new Exception("Nombre de usuario y contraseña no válido");
        }

      }

      
    }
  }
}
