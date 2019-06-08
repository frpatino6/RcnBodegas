using System;

namespace Rcn.Bodegas.Core.Exceptions
{
  public class CompanyExceptions : Exception
  {
    public CompanyExceptions()
    {
    }

    public CompanyExceptions(string userName) : base($"No company associated with the user were found { userName}")
    {
    }
    public CompanyExceptions(string companyName, string message) : base(message)
    {
    }
  }
}
