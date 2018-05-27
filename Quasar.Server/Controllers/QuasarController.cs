using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Quasar.Server.Services;

namespace Quasar.Server.Controllers {
    [Route("api/quasar")]
    [ApiController]
    public class QuasarController : ControllerBase {
        QuasarService service;

        public QuasarController(QuasarService service) {
            this.service = service;
        }

        [HttpGet("scalar")]
        public ActionResult<IEnumerable<string>> ListScalarTypes() {
            return Ok(service.Solution.ScalarResultNames);
        }

        [HttpPost("scalar")]
        public async Task<ActionResult<List<double>>> CalcScalar(CalcOptions options) {
            return await service.CalculateScalarResult(options);
        }

        [HttpGet("vector")]
        public ActionResult<IEnumerable<string>> ListVectorTypes() {
            return Ok(service.Solution.VectorResultNames);
        }

        [HttpPost("vector")]
        public async Task<ActionResult<List<Vector3D>>> CalcVector(CalcOptions options) {
            return await service.CalculateVectorResult(options);
        }

        [HttpPost("load")]
        public async Task<ActionResult> Load(LoadOptions options) {
            await service.Load(options);
            return Ok();
        }
    }
}
