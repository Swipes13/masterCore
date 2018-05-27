using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Quasar.Server.Services {
    public class LoadOptions {
        public LoadOptions() {}
        public LoadOptions(String pp, String sp) {
            ProjectPath = pp;
            SolPath = sp;
        }
        public string ProjectPath { get; set; }
        public string SolPath { get; set; }
    }

    public class CalcOptions {
        public IEnumerable<Vector3D> Points { get; set; }
        public double t { get; set; }
        public string ResultName { get; set; }
    }

    public class QuasarService {
        public Quasar.UI.Project.Project Project { get; set; }
        public Quasar.UI.Project.ISolutionView Solution { get; set; }

        public async Task Load(LoadOptions options) {
            Console.WriteLine("load!");
            var lp = new LoadOptions("../quasar/Panda.quasar", "../quasar/panda-BemProblem.sol");
            // service.Load();
            Project = await Quasar.UI.Project.Project.LoadAsync("test", File.OpenRead(lp.ProjectPath));
            var problem = Project.Items.OfType<Quasar.UI.Project.ProblemView>().First();
            Solution = await problem.LoadSolution(lp.SolPath);
        }

        public Task<List<double>> CalculateScalarResult(CalcOptions options) {
            return Task.Run(() => {
                var sol = Solution.GetScalarSolutionWithTime(options.ResultName);
                return options.Points.Select(p => sol(p, options.t, null)).ToList();
            });
        }

        public Task<List<Vector3D>> CalculateVectorResult(CalcOptions options) {
            return Task.Run(() => {
                var sol = Solution.GetVectorSolutionWithTime(options.ResultName);
                return options.Points.Select(p => sol(p, options.t, null)).ToList();
            });
        }
    }
}
