import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.Category

// --- Composable CategoryItem (Asegúrate que exista y use M3 si quieres consistencia) ---
@Composable
fun CategoryItem(
    category: Category,
    navController: NavController,
    onEdit: (Category) -> Unit,
    onDelete: (Category) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("products/${category.id}") }, // Navega a productos de esta categoría
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevación M3
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray) // Ejemplo de borde M3
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = category.image, // URL de la imagen de la categoría
                contentDescription = category.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                // Puedes añadir placeholders/error handling para Coil aquí
                // placeholder = painterResource(id = R.drawable.placeholder),
                // error = painterResource(id = R.drawable.error_image)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { onEdit(category) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Categoría")
            }
            IconButton(onClick = { onDelete(category) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar Categoría", tint = Color.Red)
            }
        }
    }
}